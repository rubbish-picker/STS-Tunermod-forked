package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RewriteAction;
import tuner.helpers.ModHelper;
import tuner.powers.RushPower;

public class StandingAttack extends MouldCard{
    public StandingAttack(){
        super(StandingAttack.class.getSimpleName(), 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m){
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));
        if(ModHelper.canRewrote())
            addToBot(new RewriteAction(p.drawPile.getTopCard(), true));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(6);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new StandingAttack();
    }
}
