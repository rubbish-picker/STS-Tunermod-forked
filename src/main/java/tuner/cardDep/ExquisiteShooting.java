package tuner.cardDep;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import tuner.cards.MouldCard;
import tuner.powers.RushPower;

public class ExquisiteShooting extends MouldCard {
    public ExquisiteShooting() {
        super(ExquisiteShooting.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 14;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.hasPower(RushPower.POWER_ID)) {
            addToBot(new RemoveAllBlockAction(m, p));

            if (p.getPower(RushPower.POWER_ID).amount >= 5) {
                addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                        AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
            }

            if (p.getPower(RushPower.POWER_ID).amount >= 15) {
                addToBot(new ApplyPowerAction(p, p, new WeakPower(p, 1, false)));
            }
        }
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
        return new ExquisiteShooting();
    }
}
