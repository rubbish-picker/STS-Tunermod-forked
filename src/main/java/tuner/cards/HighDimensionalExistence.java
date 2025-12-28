package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.effects.FormEffect;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.HighDimensionalExistencePower;

public class HighDimensionalExistence extends MouldCard implements FullArtSubscriber {
    public HighDimensionalExistence(){
        super(HighDimensionalExistence.class.getSimpleName(), 2, CardType.POWER, CardRarity.RARE, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.sound.play("HighDimensionalExistence");
                this.isDone = true;
            }
        });
        addToBot(new VFXAction(new FormEffect(p.hb.cX, p.hb.cY)));
        addToBot(new ApplyPowerAction(p,p,new HighDimensionalExistencePower(p, 1)));
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
        return new HighDimensionalExistence();
    }
}
