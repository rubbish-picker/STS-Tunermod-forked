package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.colorless.Star;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.AstrologerPower;
import tuner.powers.RushPower;

public class Astrologer extends MouldCard implements FullArtSubscriber {
    public Astrologer(){
        super(Astrologer.class.getSimpleName(), 1, CardType.POWER, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
        this.cardsToPreview = new Star();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new ApplyPowerAction(p,p,new AstrologerPower(p, this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = DESCRIPTION_UPG;
            this.initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Astrologer();
    }
}
