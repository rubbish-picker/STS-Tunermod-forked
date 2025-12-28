package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.helpers.ModHelper;
import tuner.powers.ARPower;
import tuner.relics.ATRelic;

public class Resonance extends MouldCard{
    public Resonance(){
        super(Resonance.class.getSimpleName(), 0, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if(ModHelper.canRewrote()){
                    ATRelic.at.MaxCount += magicNumber;
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Resonance();
    }
}
