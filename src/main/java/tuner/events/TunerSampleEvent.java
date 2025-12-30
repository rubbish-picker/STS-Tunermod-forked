package tuner.events;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import tuner.helpers.ModHelper;
import tuner.llm.LLMEffect;
import tuner.llm.LLMEffectType;
import tuner.llm.LLMEventExecutor;

public class TunerSampleEvent extends AbstractImageEvent {
    public static final String ID = ModHelper.makeID(TunerSampleEvent.class.getSimpleName());

    private static final EventStrings STRINGS = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = STRINGS.NAME;
    private static final String[] DESCRIPTIONS = STRINGS.DESCRIPTIONS;
    private static final String[] OPTIONS = STRINGS.OPTIONS;

    private static final String IMG = "tunerResources/img/event/epcg/blueBg.jpg";

    private static final int GOLD_GAIN = 50;

    private boolean waitingForCardReward = false;

    private Screen screen = Screen.INTRO;

    private enum Screen {
        INTRO,
        DONE
    }

    public TunerSampleEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        this.imageEventText.setDialogOption(String.format(OPTIONS[0], GOLD_GAIN));
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    public void update() {
        super.update();

        if (waitingForCardReward) {
            boolean wasWaiting = LLMEventExecutor.isWaitingForCardReward();
            LLMEventExecutor.update();
            if (wasWaiting && !LLMEventExecutor.isWaitingForCardReward()) {
                waitingForCardReward = false;
                this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                this.imageEventText.clearAllDialogs();
                this.imageEventText.setDialogOption(OPTIONS[3]);
                screen = Screen.DONE;
            }
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screen) {
            case INTRO:
                if (buttonPressed == 0) {
                    AbstractDungeon.player.gainGold(GOLD_GAIN);
                    logMetricGainGold(ID, "TAKE_GOLD", GOLD_GAIN);

                    this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                    this.imageEventText.clearAllDialogs();
                    this.imageEventText.setDialogOption(OPTIONS[3]);
                    screen = Screen.DONE;
                } else if (buttonPressed == 1) {
                    LLMEffect eff = new LLMEffect();
                    eff.type = LLMEffectType.CARD_REWARD;
                    eff.prompt = OPTIONS[4];
                    LLMEventExecutor.executeEffect(eff);
                    waitingForCardReward = true;
                } else {
                    logMetricIgnored(ID);
                    openMap();
                }
                break;
            case DONE:
                openMap();
                break;
        }
    }
}
