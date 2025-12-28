package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;

import tuner.action.GainRushPowerAction;
import tuner.interfaces.OnRightClickInHandSubscriber;

public class PhantomStep extends MouldCard implements OnRightClickInHandSubscriber {
    public PhantomStep() {
        super(PhantomStep.class.getSimpleName(), 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 3;
        this.block = this.baseBlock = 7;
    }

    @Override
    public void onRightClickInHand() {
        if (!AbstractDungeon.player.isDraggingCard)
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    addToTop(new GainRushPowerAction(PhantomStep.this, PhantomStep.this.magicNumber));
                    if (AbstractDungeon.player.hand.contains(PhantomStep.this))
                        AbstractDungeon.player.hand.moveToDiscardPile(PhantomStep.this);

                    // PhantomStep.this.onUse = false;
                    this.isDone = true;
                }
            });
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, this.block));
        for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters)
            addToBot(new ApplyPowerAction(mo, p, new WeakPower(mo, 1, false)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            upgradeBlock(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PhantomStep();
    }
}
