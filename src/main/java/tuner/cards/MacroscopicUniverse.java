package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.relics.ATRelic;

public class MacroscopicUniverse extends MouldCard {
    public MacroscopicUniverse() {
        super(MacroscopicUniverse.class.getSimpleName(), 2, CardType.POWER, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
//        addToBot(new ApplyPowerAction(p,p,new MacroscopicUniversePower(p, this.magicNumber)));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard c : ATRelic.at.dorlach.dorlachGroup.group) {
                    c.upgrade();
                }
                this.isDone = true;
            }
        });
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
        return new MacroscopicUniverse();
    }
}
