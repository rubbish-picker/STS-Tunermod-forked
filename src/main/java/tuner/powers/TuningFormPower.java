package tuner.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.helpers.ModHelper;

public class TuningFormPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(TuningFormPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";

    private boolean justUsed = true;

    public TuningFormPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = amount;
        loadRegion("amplify");
        this.updateDescription();
    }

    @Override
    public void onCardDraw(AbstractCard card) {
        if (!this.justUsed) {
            flash();
            this.justUsed = true;

            for (int i = 0; i < this.amount; i++) {
                AbstractCard temp = card.makeStatEquivalentCopy();
                temp.freeToPlayOnce = true;
                this.addToTop(new MakeTempCardInHandAction(temp));
            }
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (AbstractDungeon.player.drawPile.size() + AbstractDungeon.player.discardPile.size() > 0) {
            this.justUsed = false;
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    addToBot(new DrawCardAction(1));
                    this.isDone = true;
                }
            });
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}