package tuner.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.powers.SanShengTiaoPower;

public class SanShengTiao extends MouldCard{
    public SanShengTiao(){
        super(SanShengTiao.class.getSimpleName(), 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 18;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new ApplyPowerAction(p,p,new SanShengTiaoPower(p, this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(9);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SanShengTiao();
    }
}
