package tuner.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.misc.MapcardTarget;
import tuner.powers.FusionPower;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Fusion extends MouldCard {
    public Fusion() {
        super(Fusion.class.getSimpleName(), 2, CardType.POWER, CardRarity.RARE, MapCard);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
//        addToBot(new AbstractGameAction() {
//            private boolean once = true;
//
//            @Override
//            public void update() {
//                if (once) {
//                    once = false;
//                    if (p.drawPile.isEmpty()) {
//                        this.isDone = true;
//                        return;
//                    }
//                    AbstractDungeon.gridSelectScreen.open(p.drawPile, 1, cardStrings.EXTENDED_DESCRIPTION[0], false);
//                } else {
//                    if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
//                        addToTop(new ApplyPowerAction(p, p, new FusionPower(
//                                AbstractDungeon.gridSelectScreen.selectedCards.get(0))));
//                    }
//                    this.isDone = true;
//                }
//            }
//        });

        AbstractCard temp = MapcardTarget.getTarget(this);
        if (temp != null) {
            addToTop(new ApplyPowerAction(p, p, new FusionPower(temp)));
        }
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return !p.drawPile.isEmpty() && super.canUse(p, m);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fusion();
    }
}
