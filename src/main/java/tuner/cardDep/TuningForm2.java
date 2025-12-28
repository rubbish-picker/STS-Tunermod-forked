package tuner.cardDep;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.powers.TuningFormPower2;

public class TuningForm2 extends MouldCard {
    public TuningForm2(){
        super(TuningForm2.class.getSimpleName(), 3, CardType.POWER, CardRarity.RARE, CardTarget.NONE);
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new ApplyPowerAction(p,p,new TuningFormPower2(p, 1)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isEthereal = false;
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TuningForm2();
    }
}
