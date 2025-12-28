package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.ARPower;
import tuner.powers.GenrokuPower;

public class Genroku extends MouldCard implements FullArtSubscriber {
    public Genroku(){
        super(Genroku.class.getSimpleName(), 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 6;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.sound.play("Piaofu");
                this.isDone = true;
            }
        });
        addToBot(new ApplyPowerAction(p,p,new GenrokuPower(p, this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Genroku();
    }
}
