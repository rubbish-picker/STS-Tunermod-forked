package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.effects.TunerMoveEffect;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.RandomSongPower;
import tuner.powers.RushPower;

public class RandomSong extends MouldCard implements FullArtSubscriber {
    public RandomSong(){
        super(RandomSong.class.getSimpleName(), 2, CardType.SKILL, CardRarity.RARE, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new VFXAction(new TunerMoveEffect(m.hb.cX, m.hb.cY)));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.sound.play("ATTACK_POISON");
                this.isDone = true;
            }
        });
        addToBot(new ApplyPowerAction(m,p,new RandomSongPower(m, this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(6);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new RandomSong();
    }
}
