package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.TuningFormPower3;

public class TuningForm3 extends MouldCard implements FullArtSubscriber {
    public TuningForm3() {
        super(TuningForm3.class.getSimpleName(), 3, CardType.POWER, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.sound.play("tunerForm");
                this.isDone = true;
            }
        });
        addToBot(new ApplyPowerAction(p, p, new TuningFormPower3(p, this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isEthereal = false;
            this.rawDescription = DESCRIPTION_UPG;
            this.initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TuningForm3();
    }
}
