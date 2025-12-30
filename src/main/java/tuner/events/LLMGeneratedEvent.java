package tuner.events;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tuner.helpers.ModHelper;
import tuner.llm.LLMEventExecutor;
import tuner.llm.LLMEventOption;
import tuner.llm.LLMEventService;
import tuner.llm.LLMEventSpec;

import java.util.ArrayList;

public class LLMGeneratedEvent extends AbstractImageEvent {
    private static final Logger logger = LogManager.getLogger(LLMGeneratedEvent.class);
    public static final String ID = ModHelper.makeID(LLMGeneratedEvent.class.getSimpleName());

    // Localized UI strings for this generated event
    private static final com.megacrit.cardcrawl.localization.EventStrings STRINGS = CardCrawlGame.languagePack.getEventString("tuner:LLMGeneratedEvent");
    private static final String LEAVE_OPTION = (STRINGS != null && STRINGS.OPTIONS != null && STRINGS.OPTIONS.length > 0) ? STRINGS.OPTIONS[0] : "[Leave]";
    private static final String CONTINUE_OPTION = (STRINGS != null && STRINGS.OPTIONS != null && STRINGS.OPTIONS.length > 1) ? STRINGS.OPTIONS[1] : "[Continue]";

    // Pool of vanilla event images to randomly select from
    private static final String[] EVENT_IMAGES = {
        "images/events/bigFish.jpg",
        "images/events/bonfire.jpg",
        "images/events/cleric.jpg",
        "images/events/dead.jpg",
        "images/events/drugsDealer.jpg",
        "images/events/goldenIdol.jpg",
        "images/events/goldenWing.jpg",
        "images/events/liars.jpg",
        "images/events/masks.jpg",
        "images/events/mushrooms.jpg",
        "images/events/nest.jpg",
        "images/events/portal.jpg",
        "images/events/purchaser.jpg",
        "images/events/redMask.jpg",
        "images/events/scrap.jpg",
        "images/events/sensory.jpg",
        "images/events/shrine1.jpg",
        "images/events/shrine2.jpg",
        "images/events/shrine3.jpg",
        "images/events/shrine4.jpg",
        "images/events/sphereBlue.jpg",
        "images/events/sphereGold.jpg",
        "images/events/vampires.jpg",
        "images/events/wheelOfChange.jpg",
        "images/events/wingedStatue.jpg"
    };

    private static String getRandomEventImage() {
        // Choose a random event image that actually exists. If none of the vanilla images exist
        // on this install (some users may run without base assets), fall back to a bundled mod image.
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String s : EVENT_IMAGES) list.add(s);
        java.util.Collections.shuffle(list);

        for (String path : list) {
            try {
                if (com.badlogic.gdx.Gdx.files.internal(path).exists()) {
                    logger.info("[LLM] Selected event image: {}", path);
                    return path;
                }
            } catch (Exception e) {
                // Gdx may not be initialized in some headless contexts; ignore errors and continue
                logger.debug("[LLM] Could not check image {}: {}", path, e.toString());
            }
        }

        // Fallback to a mod-bundled image
        String fallback = "tunerResources/img/event/epcg/blueBg.jpg";
        logger.warn("[LLM] No vanilla event image found; falling back to {}", fallback);
        return fallback;
    }

    private final LLMEventSpec spec;

    private boolean waitingForCardReward = false;
    private boolean done = false;

    // Pending combat execution triggered after showing revealText (small delay to allow UI update)
    private java.util.ArrayList<tuner.llm.LLMEffect> pendingCombatEffects = null;
    private float pendingCombatDelay = 0f;
    private String pendingCombatRevealText = null;
        // Pending hidden effects that should execute after a card reward finishes
        private java.util.ArrayList<tuner.llm.LLMEffect> pendingHiddenEffects = null;
        private String pendingHiddenRevealText = null;
        private boolean revealBlocking = false; // when true, ignore option clicks while waiting to start combat

        // Tracks that we started a combat from this event so we can handle post-combat cleanup
        private boolean startedCombatFromEvent = false;

    public LLMGeneratedEvent() {
        this(LLMEventService.getForFloor(AbstractDungeon.floorNum));
    }

    public LLMGeneratedEvent(LLMEventSpec spec) {
        super(
                (spec != null && spec.name != null) ? replacePlaceholders(spec.name) : "LLM Event",
                (spec != null && spec.descriptions != null && !spec.descriptions.isEmpty()) ? replacePlaceholders(spec.descriptions.get(0)) : "...",
                getRandomEventImage()
        );

        // Create display-time copy after super
        LLMEventSpec displaySpec = spec == null ? null : makeDisplaySpecFrom(spec);
        // Keep the display spec for UI (this.spec contains the display-ready texts)
        this.spec = displaySpec != null ? displaySpec : spec;

        if (this.spec == null || this.spec.options == null || this.spec.options.isEmpty()) {
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(LEAVE_OPTION);
            return;
        }

        this.imageEventText.clearAllDialogs();
        for (int i = 0; i < this.spec.options.size(); i++) {
            LLMEventOption opt = this.spec.options.get(i);
            String text = opt == null || opt.text == null ? CONTINUE_OPTION : opt.text;
            logger.info("[LLM] Option {} text (display): {}", i, text);
            this.imageEventText.setDialogOption(text);
        }
    }

    @Override
    public void update() {
        super.update();

        // Handle pending card reward flow
        if (waitingForCardReward) {
            boolean wasWaiting = LLMEventExecutor.isWaitingForCardReward();
            LLMEventExecutor.update();
            if (wasWaiting && !LLMEventExecutor.isWaitingForCardReward()) {
                // Card reward finished. If we have hidden effects that were deferred until after the reward,
                // execute them now (possibly triggering combat or another card reward).
                if (pendingHiddenEffects != null && !pendingHiddenEffects.isEmpty()) {
                    java.util.ArrayList<tuner.llm.LLMEffect> hidden = pendingHiddenEffects;
                    String reveal = pendingHiddenRevealText;
                    pendingHiddenEffects = null;
                    pendingHiddenRevealText = null;
                    logger.info("[LLM] Executing {} deferred hidden effects after card reward", hidden.size());

                    boolean containsCombat = false;
                    for (tuner.llm.LLMEffect he : hidden) {
                        if (he != null && he.type != null && (he.type == tuner.llm.LLMEffectType.START_COMBAT || he.type == tuner.llm.LLMEffectType.START_HARD_COMBAT)) {
                            containsCombat = true;
                            break;
                        }
                    }

                    if (containsCombat) {
                        // Show revealText then defer to pendingCombat mechanism to start combat
                        if (reveal != null && !reveal.isEmpty()) {
                            this.imageEventText.updateBodyText(replacePlaceholders(reveal));
                        } else if (spec != null && spec.descriptions != null && spec.descriptions.size() >= 2) {
                            this.imageEventText.updateBodyText(replacePlaceholders(spec.descriptions.get(1)));
                        }

                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(LEAVE_OPTION);

                        pendingCombatEffects = new java.util.ArrayList<>(hidden);
                        pendingCombatRevealText = reveal;
                        pendingCombatDelay = 0.2f;
                        revealBlocking = true;
                    } else {
                        // No combat: execute immediately
                        LLMEventExecutor.executeEffects(hidden);

                        // If executing hidden effects started combat, transition
                        if (LLMEventExecutor.isWaitingForCombat()) {
                            enterCombatFromEvent();
                            waitingForCardReward = false;
                            return;
                        }

                        if (LLMEventExecutor.isWaitingForCardReward()) {
                            // It started a nested card reward; keep waiting
                            waitingForCardReward = true;
                            return;
                        }

                        // Otherwise, show reveal and leave
                        showTrapRevealAndLeave(reveal);
                        waitingForCardReward = false;
                        return;
                    }
                }

                // No deferred hidden effects: complete as before
                waitingForCardReward = false;
                showDoneAndLeave();
            }
        }

        // Handle delayed combat start after showing reveal text
        if (pendingCombatEffects != null) {
            pendingCombatDelay -= com.badlogic.gdx.Gdx.graphics.getDeltaTime();
            if (pendingCombatDelay <= 0f) {
                java.util.ArrayList<tuner.llm.LLMEffect> toExec = pendingCombatEffects;
                pendingCombatEffects = null;
                // Execute hidden effects now (this may start combat via LLMEventExecutor.startCombat)
                logger.info("[LLM] Executing deferred {} hidden effects", toExec == null ? 0 : toExec.size());
                LLMEventExecutor.executeEffects(toExec);

                // Reset blocking regardless
                revealBlocking = false;

                // If a combat was started, immediately transition to combat
                if (LLMEventExecutor.isWaitingForCombat()) {
                    // Enter combat after starting it
                    enterCombatFromEvent();
                    startedCombatFromEvent = true;
                    return;
                }

                // No combat started, show reveal+leave
                showTrapRevealAndLeave(pendingCombatRevealText);
                pendingCombatRevealText = null;
            }
        }

        // Handle post-combat cleanup: if we started combat from this event and the room is now over
        if (startedCombatFromEvent) {
            com.megacrit.cardcrawl.rooms.AbstractRoom room = com.megacrit.cardcrawl.dungeons.AbstractDungeon.getCurrRoom();
            if (room != null && room.isBattleOver) {
                // Reset flag and show post-combat text with leave option
                startedCombatFromEvent = false;
                showDoneAndLeave();
            }
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (revealBlocking) {
            // Ignore input while we're showing reveal text and about to start combat
            return;
        }

        if (done) {
            openMap();
            return;
        }

        if (spec == null || spec.options == null || spec.options.isEmpty()) {
            openMap();
            return;
        }

        if (buttonPressed < 0 || buttonPressed >= spec.options.size()) {
            openMap();
            return;
        }

        LLMEventOption opt = spec.options.get(buttonPressed);
        if (opt != null) {
            try {
                // First execute visible effects (the bait)
                LLMEventExecutor.executeEffects(opt.effects);

                // Check if combat was started by visible effects
                if (LLMEventExecutor.isWaitingForCombat()) {
                    enterCombatFromEvent();
                    return;
                }

                if (LLMEventExecutor.isWaitingForCardReward()) {
                    waitingForCardReward = true;
                    // Defer hidden effects until after card reward finishes
                    if (opt.hiddenEffects != null && !opt.hiddenEffects.isEmpty()) {
                        pendingHiddenEffects = new java.util.ArrayList<>(opt.hiddenEffects);
                        pendingHiddenRevealText = opt.revealText;
                        logger.info("[LLM] Deferred {} hidden effects until after card reward", pendingHiddenEffects.size());
                    }
                    return;
                }

                // Now execute hidden effects (the trap!)
                if (opt.hiddenEffects != null && !opt.hiddenEffects.isEmpty()) {
                    logger.info("[LLM] Found {} hidden effects for option {}", opt.hiddenEffects.size(), buttonPressed);

                    // If hidden effects include START_COMBAT/START_HARD_COMBAT, show reveal text first and defer execution
                    boolean containsCombat = false;
                    for (tuner.llm.LLMEffect he : opt.hiddenEffects) {
                        if (he != null && he.type != null && (he.type == tuner.llm.LLMEffectType.START_COMBAT || he.type == tuner.llm.LLMEffectType.START_HARD_COMBAT)) {
                            containsCombat = true;
                            break;
                        }
                    }

                    if (containsCombat) {
                        // Show revealText (use placeholders) to the player before the combat begins
                        if (opt.revealText != null && !opt.revealText.isEmpty()) {
                            this.imageEventText.updateBodyText(replacePlaceholders(opt.revealText));
                        } else if (spec != null && spec.descriptions != null && spec.descriptions.size() >= 2) {
                            this.imageEventText.updateBodyText(replacePlaceholders(spec.descriptions.get(1)));
                        }

                        // Clear dialogs and provide leave option while we prepare to start combat
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(LEAVE_OPTION);

                        // Defer execution slightly to allow UI to render revealText and block clicks
                        pendingCombatEffects = new java.util.ArrayList<>(opt.hiddenEffects);
                        pendingCombatRevealText = opt.revealText;
                        pendingCombatDelay = 0.2f; // small delay
                        revealBlocking = true;
                        return;
                    }

                    // No combat in hidden effects: execute them immediately
                    LLMEventExecutor.executeEffects(opt.hiddenEffects);

                    // If executing hidden effects started combat, transition
                    if (LLMEventExecutor.isWaitingForCombat()) {
                        enterCombatFromEvent();
                        return;
                    }

                    if (LLMEventExecutor.isWaitingForCardReward()) {
                        waitingForCardReward = true;
                        return;
                    }

                    // Show reveal text and leave
                    showTrapRevealAndLeave(opt.revealText);
                    return;
                }
            } catch (Exception ignored) {
                openMap();
                return;
            }
        }

        showDoneAndLeave();
    }

    /**
     * Show the trap reveal text and prepare to leave.
     */
    private void showTrapRevealAndLeave(String revealText) {
        done = true;
        
        String toShow = null;
        if (revealText != null && !revealText.isEmpty()) {
            toShow = replacePlaceholders(revealText);
        } else if (spec != null && spec.descriptions != null && spec.descriptions.size() >= 2) {
            toShow = replacePlaceholders(spec.descriptions.get(1));
        } else {
            toShow = "...";
        }
        
        this.imageEventText.updateBodyText(toShow);
        
        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(LEAVE_OPTION);
    }

    /**
     * Properly transition from event UI to combat.
     */
    private void enterCombatFromEvent() {
        // Hide event dialog
        GenericEventDialog.hide();
        this.imageEventText.clearAllDialogs();
        
        // Mark that we're in combat mode
        this.hasFocus = false;
        this.combatTime = true;
        
        // The combat was already initiated by LLMEventExecutor.startCombat()
        // We just need to clear the waiting flag since combat is now running
        LLMEventExecutor.clearWaitingStates();

        // Remember that this event caused the combat so we can handle post-combat cleanup
        startedCombatFromEvent = true;
    }

    /**
     * Replace placeholders in text with current run values.
     * Supported placeholders: {HP}, {MAX_HP}, {GOLD}, {DECK_SIZE}, {CURSE_COUNT}, {RELICS}, {FLOOR}
     */
    private static String replacePlaceholders(String s) {
        if (s == null) return null;
        try {
            com.megacrit.cardcrawl.characters.AbstractPlayer p = com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;
            if (p == null) return s;

            String res = s;
            res = res.replace("{HP}", Integer.toString(p.currentHealth));
            res = res.replace("{MAX_HP}", Integer.toString(p.maxHealth));
            res = res.replace("{GOLD}", Integer.toString(p.gold));
            res = res.replace("{FLOOR}", Integer.toString(com.megacrit.cardcrawl.dungeons.AbstractDungeon.floorNum));
            
            // Normalize color tokens after substitution to handle cases like fullwidth hashes or uppercase tokens
            res = tuner.llm.LLMEventValidator.normalizeColorTokens(res);

            int deckSize = p.masterDeck == null ? 0 : p.masterDeck.group == null ? 0 : p.masterDeck.group.size();
            res = res.replace("{DECK_SIZE}", Integer.toString(deckSize));

            int curseCount = 0;
            if (p.masterDeck != null && p.masterDeck.group != null) {
                for (com.megacrit.cardcrawl.cards.AbstractCard c : p.masterDeck.group) {
                    if (c != null && c.type == com.megacrit.cardcrawl.cards.AbstractCard.CardType.CURSE) curseCount++;
                }
            }
            res = res.replace("{CURSE_COUNT}", Integer.toString(curseCount));

            // Relics list (comma-separated up to 6)
            String relics = "";
            if (p.relics != null && !p.relics.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int n = 0;
                for (com.megacrit.cardcrawl.relics.AbstractRelic r : p.relics) {
                    if (r == null) continue;
                    if (n > 0) sb.append(", ");
                    sb.append(r.name);
                    n++;
                    if (n >= 6) break;
                }
                relics = sb.toString();
            } else {
                relics = "none";
            }
            res = res.replace("{RELICS}", relics);

            return res;
        } catch (Exception e) {
            logger.warn("[LLM] Failed to replace placeholders: {}", e.toString());
            return s;
        }
    }

    private LLMEventSpec makeDisplaySpecFrom(LLMEventSpec src) {
        if (src == null) return null;
        LLMEventSpec dst = new LLMEventSpec();
        dst.name = replacePlaceholders(src.name);
        if (src.descriptions != null) {
            for (String d : src.descriptions) {
                dst.descriptions.add(replacePlaceholders(d));
            }
        }
        if (src.options != null) {
            for (LLMEventOption o : src.options) {
                LLMEventOption no = new LLMEventOption();
                no.text = replacePlaceholders(o.text);
                no.revealText = replacePlaceholders(o.revealText);
                if (o.effects != null) no.effects = new ArrayList<>(o.effects);
                if (o.hiddenEffects != null) no.hiddenEffects = new ArrayList<>(o.hiddenEffects);
                dst.options.add(no);
            }
        }
        return dst;
    }

    private void showDoneAndLeave() {
        done = true;

        if (spec != null && spec.descriptions != null && spec.descriptions.size() >= 2) {
            this.imageEventText.updateBodyText(spec.descriptions.get(1));
        } else {
            this.imageEventText.updateBodyText("...");
        }

        this.imageEventText.clearAllDialogs();
        this.imageEventText.setDialogOption(LEAVE_OPTION);
    }
}
