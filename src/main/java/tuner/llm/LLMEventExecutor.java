package tuner.llm;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class LLMEventExecutor {
    private static final Logger logger = LogManager.getLogger(LLMEventExecutor.class);

    private static final int MAX_GOLD = 200;
    private static final int MAX_HEAL = 30;

    private static boolean waitingForCardReward = false;
    private static boolean waitingForCombat = false;

    // Whitelist of safe encounter keys that can be triggered from LLM events
    private static final Set<String> ALLOWED_ENCOUNTERS = new HashSet<>(Arrays.asList(
            // Exordium encounters
            "2 Louse",
            "Gremlin Gang",
            "Large Slime",
            "Looter",
            "Exordium Thugs",
            "Exordium Wildlife",
            "3 Louse",
            "2 Fungi Beasts",
            "Small Slimes",
            "Blue Slaver",
            "Cultist",
            "Jaw Worm",
            // City encounters
            "Red Slaver",
            "3 Byrds",
            "Chosen",
            "Chosen and Byrds",
            "Sentry and Sphere",
            "Snake Plant",
            "Snecko",
            "Centurion and Healer",
            "Shelled Parasite",
            "Spheric Guardian",
            // Beyond encounters
            "3 Darklings",
            "Orb Walker",
            "3 Shapes",
            "Jaw Worm Horde",
            "Maw",
            "Spire Growth",
            "Transient",
            "4 Shapes",
            "Reptomancer",
            "Writhing Mass"
    ));

    private LLMEventExecutor() {
    }

    public static boolean isWaitingForCardReward() {
        return waitingForCardReward;
    }

    public static boolean isWaitingForCombat() {
        return waitingForCombat;
    }

    public static void clearWaitingStates() {
        waitingForCardReward = false;
        waitingForCombat = false;
    }

    public static void executeEffects(ArrayList<LLMEffect> effects) {
        if (effects == null) return;
        logger.info("[LLM] executeEffects: executing {} effects", effects.size());
        int i = 0;
        for (LLMEffect effect : effects) {
            i++;
            try {
                logger.info("[LLM] executeEffects: effect #{} = {}", i, effect == null ? "null" : effect.type);
                executeEffect(effect);
            } catch (Exception e) {
                logger.error("[LLM] executeEffects: effect #{} threw exception: {}", i, e.toString());
                // Continue with remaining effects
            }
        }
    }

    public static void executeEffect(LLMEffect effect) {
        if (effect == null || effect.type == null) return;

        logger.info("[LLM] executeEffect: executing {}", effect.type);

        switch (effect.type) {
            // Positive effects
            case GAIN_GOLD:
                gainGold(effect.amount);
                break;
            case HEAL:
                heal(effect.amount);
                break;
            case CARD_REWARD:
                openCardReward(effect.prompt);
                break;
            case GIVE_RELIC:
                giveRandomRelic(effect.rarity);
                break;
            case START_COMBAT:
                startCombat(effect.encounterKey, false);
                break;
            case GAIN_MAX_HP:
                gainMaxHp(effect.amount);
                break;
            
            // Negative/hidden effects
            case LOSE_GOLD:
                loseGold(effect.amount);
                break;
            case LOSE_HP:
                loseHp(effect.amount);
                break;
            case GAIN_CURSE:
                gainCurse();
                break;
            case LOSE_MAX_HP:
                loseMaxHp(effect.amount);
                break;
            case START_HARD_COMBAT:
                startCombat(effect.encounterKey, true);
                break;
        }
    }

    public static void update() {
        if (!waitingForCardReward) return;

        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard chosen = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                    chosen, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            waitingForCardReward = false;
        }
    }

    // ========== POSITIVE EFFECTS ==========

    private static void gainGold(Integer amount) {
        if (AbstractDungeon.player == null) return;
        int value = clamp(amount == null ? 0 : amount, 0, MAX_GOLD);
        if (value > 0) {
            AbstractDungeon.player.gainGold(value);
        }
    }

    private static void heal(Integer amount) {
        if (AbstractDungeon.player == null) return;
        int value = clamp(amount == null ? 0 : amount, 0, MAX_HEAL);
        if (value > 0) {
            AbstractDungeon.player.heal(value);
        }
    }

    private static void gainMaxHp(Integer amount) {
        if (AbstractDungeon.player == null) return;
        int value = amount == null ? 0 : Math.abs(amount);
        value = Math.min(value, 15); // Cap at 15 max HP gain
        if (value > 0) {
            AbstractDungeon.player.increaseMaxHp(value, true);
            logger.info("GAIN_MAX_HP: Player gained {} max HP", value);
        }
    }

    private static void openCardReward(String prompt) {
        if (waitingForCardReward) return;
        if (AbstractDungeon.player == null) return;

        ArrayList<AbstractCard> cards = AbstractDungeon.getRewardCards();
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (AbstractCard c : cards) {
            group.addToBottom(c);
        }

        String msg = prompt;
        if (msg == null || msg.isEmpty()) {
            msg = CardCrawlGame.languagePack.getUIString("tuner:ImaginaryReward").TEXT[0];
        }

        AbstractDungeon.gridSelectScreen.open(group, 1, false, msg);
        waitingForCardReward = true;
    }

    private static void giveRandomRelic(String rarity) {
        if (AbstractDungeon.player == null) return;
        if (AbstractDungeon.getCurrRoom() == null) return;

        AbstractRelic.RelicTier tier = parseRelicTier(rarity);
        AbstractRelic relic = AbstractDungeon.returnRandomRelic(tier);
        if (relic == null) return;

        AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, relic);
    }

    // ========== NEGATIVE EFFECTS ==========

    private static void loseGold(Integer amount) {
        if (AbstractDungeon.player == null) return;
        int value = amount == null ? 0 : Math.abs(amount);
        value = Math.min(value, AbstractDungeon.player.gold);
        if (value > 0) {
            AbstractDungeon.player.loseGold(value);
            logger.info("LOSE_GOLD: Player lost {} gold", value);
        }
    }

    private static void loseHp(Integer amount) {
        if (AbstractDungeon.player == null) return;
        int value = amount == null ? 0 : Math.abs(amount);
        value = Math.min(value, 50); // Cap at 50 damage
        // Ensure non-lethal: leave at least 1 HP
        value = Math.min(value, AbstractDungeon.player.currentHealth - 1);
        if (value > 0) {
            AbstractDungeon.player.damage(new com.megacrit.cardcrawl.cards.DamageInfo(null, value, com.megacrit.cardcrawl.cards.DamageInfo.DamageType.HP_LOSS));
            logger.info("LOSE_HP: Player took {} damage (non-lethal)", value);
        }
    }

    private static void gainCurse() {
        if (AbstractDungeon.player == null) return;
        AbstractCard curse = AbstractDungeon.returnRandomCurse();
        if (curse != null) {
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(curse, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            logger.info("GAIN_CURSE: Player gained curse: {}", curse.name);
        }
    }

    private static void loseMaxHp(Integer amount) {
        if (AbstractDungeon.player == null) return;
        int value = amount == null ? 0 : Math.abs(amount);
        value = Math.min(value, 15); // Cap at 15 max HP loss
        // Ensure non-lethal: maxHP must stay above current HP and at least 1
        int minMaxHp = Math.max(AbstractDungeon.player.currentHealth, 1);
        value = Math.min(value, AbstractDungeon.player.maxHealth - minMaxHp);
        if (value > 0) {
            AbstractDungeon.player.decreaseMaxHealth(value);
            logger.info("LOSE_MAX_HP: Player lost {} max HP (non-lethal)", value);
        }
    }

    // ========== COMBAT ==========

    // Harder encounters for punishment
    private static final Set<String> HARD_ENCOUNTERS = new HashSet<>(Arrays.asList(
            "Gremlin Nob",
            "Lagavulin",
            "3 Sentries",
            "Book of Stabbing",
            "Gremlin Leader",
            "Slavers",
            "Taskmaster",
            "Reptomancer",
            "Giant Head",
            "Nemesis",
            "Maw"
    ));

    /**
     * Start a combat encounter from an event.
     * @param encounterKey the encounter to start
     * @param isHard if true, uses harder encounter from HARD_ENCOUNTERS
     */
    private static void startCombat(String encounterKey, boolean isHard) {
        if (AbstractDungeon.player == null) return;
        if (AbstractDungeon.getCurrRoom() == null) return;
        
        String key;
        if (isHard) {
            // Pick a hard encounter
            ArrayList<String> hardList = new ArrayList<>(HARD_ENCOUNTERS);
            key = hardList.get(AbstractDungeon.miscRng.random(hardList.size() - 1));
            logger.info("START_HARD_COMBAT: Selected hard encounter '{}'", key);
        } else {
            if (encounterKey == null || encounterKey.trim().isEmpty()) {
                logger.warn("START_COMBAT: No encounter key provided");
                return;
            }
            key = encounterKey.trim();
            // Validate against whitelist
            if (!ALLOWED_ENCOUNTERS.contains(key)) {
                logger.warn("START_COMBAT: Encounter '{}' not in whitelist, ignoring", key);
                return;
            }
        }

        try {
            // Get the monster group for this encounter
            AbstractDungeon.getCurrRoom().monsters = MonsterHelper.getEncounter(key);
            if (AbstractDungeon.getCurrRoom().monsters == null) {
                logger.warn("START_COMBAT: MonsterHelper returned null for '{}'", key);
                return;
            }

            // Store the encounter key for metrics
            AbstractDungeon.lastCombatMetricKey = key;

            // Set event type to ROOM (combat mode)
            AbstractEvent.type = AbstractEvent.EventType.ROOM;

            // Configure room for combat
            AbstractDungeon.getCurrRoom().cannotLose = false;
            AbstractDungeon.getCurrRoom().eliteTrigger = false;
            AbstractDungeon.getCurrRoom().rewardAllowed = true;
            AbstractDungeon.getCurrRoom().rewards.clear();
            AbstractDungeon.getCurrRoom().addGoldToRewards(AbstractDungeon.treasureRng.random(10, 20));
            AbstractDungeon.getCurrRoom().addPotionToRewards();

            // Position player
            AbstractDungeon.player.movePosition(Settings.WIDTH * 0.25F, AbstractDungeon.floorY);
            AbstractDungeon.player.flipHorizontal = false;

            // Enter combat (similar to AbstractImageEvent.enterCombat)
            AbstractDungeon.getCurrRoom().smoked = false;
            AbstractDungeon.player.isEscaping = false;
            AbstractDungeon.getCurrRoom().isBattleOver = false;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMBAT;
            AbstractDungeon.getCurrRoom().monsters.init();
            AbstractRoom.waitTimer = 0.1F;
            AbstractDungeon.player.preBattlePrep();
            CardCrawlGame.fadeIn(1.5F);
            AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;

            // Enable reward popup after combat
            AbstractDungeon.getCurrRoom().rewardTime = true;

            waitingForCombat = true;

            logger.info("START_COMBAT: Started encounter '{}'", key);

        } catch (Exception e) {
            logger.error("START_COMBAT: Failed to start encounter '{}': {}", key, e.getMessage());
        }
    }

    private static AbstractRelic.RelicTier parseRelicTier(String rarity) {
        if (rarity == null) return AbstractRelic.RelicTier.COMMON;
        String r = rarity.trim().toUpperCase();
        switch (r) {
            case "UNCOMMON":
                return AbstractRelic.RelicTier.UNCOMMON;
            case "RARE":
                return AbstractRelic.RelicTier.RARE;
            case "BOSS":
                return AbstractRelic.RelicTier.BOSS;
            case "SHOP":
                return AbstractRelic.RelicTier.SHOP;
            case "SPECIAL":
                return AbstractRelic.RelicTier.SPECIAL;
            case "STARTER":
                return AbstractRelic.RelicTier.STARTER;
            case "COMMON":
            default:
                return AbstractRelic.RelicTier.COMMON;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
