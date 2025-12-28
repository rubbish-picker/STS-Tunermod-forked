package tuner.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.colorless.Star;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.AstrologerPower;
import tuner.powers.ChasingHuntingPower;

public class ChasingHunting extends MouldCard implements FullArtSubscriber {
    public ChasingHunting(){
        super(ChasingHunting.class.getSimpleName(), 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new ApplyPowerAction(p,p,new ChasingHuntingPower(p, this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ChasingHunting();
    }
}
