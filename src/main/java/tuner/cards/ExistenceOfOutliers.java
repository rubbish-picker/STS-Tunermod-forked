package tuner.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.imaginaryColor.Deflect;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.ExistencePower;

public class ExistenceOfOutliers extends MouldCard implements FullArtSubscriber {
    public ExistenceOfOutliers(){
        super(ExistenceOfOutliers.class.getSimpleName(), 2, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
        this.cardsToPreview = new Deflect();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new ApplyPowerAction(p,p,new ExistencePower(p, this.magicNumber)));
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
        return new ExistenceOfOutliers();
    }
}
