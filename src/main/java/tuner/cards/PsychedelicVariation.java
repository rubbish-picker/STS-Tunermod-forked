package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.effects.TunerMoveEffect;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.MindControlPower;
import tuner.powers.RushPower;

public class PsychedelicVariation extends MouldCard implements FullArtSubscriber {
    public PsychedelicVariation(){
        super(PsychedelicVariation.class.getSimpleName(), 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = 1;
        this.damage = this.baseDamage = 11;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new VFXAction(new TunerMoveEffect(m.hb.cX, m.hb.cY)));
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        addToBot(new ApplyPowerAction(m,p,new MindControlPower(m,this.magicNumber)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(4);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PsychedelicVariation();
    }
}
