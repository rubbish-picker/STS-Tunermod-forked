package tuner.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.helpers.ModHelper;

public class IntelligencePreparationPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(IntelligencePreparationPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = ModHelper.makeRlcAd(IntelligencePreparationPower.class.getSimpleName(), true);
    private static final String PATH48 = ModHelper.makeRlcAd(IntelligencePreparationPower.class.getSimpleName(), false);

    public IntelligencePreparationPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;

        this.loadRegion("carddraw");

        this.updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        this.flash();
        this.addToBot(new DrawCardAction(this.amount, new AbstractGameAction() {
            @Override
            public void update() {
                DrawCardAction.drawnCards.forEach(c -> {
                    if (c.canUpgrade()) {
                        c.upgrade();
                    }
                });
                this.isDone = true;
            }
        }));
        this.addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}