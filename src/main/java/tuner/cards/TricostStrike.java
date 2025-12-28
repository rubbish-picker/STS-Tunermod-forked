package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.PummelDamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import tuner.action.GainRushPowerAction;
import tuner.powers.RushPower;

public class TricostStrike extends MouldCard {
    public TricostStrike() {
        super(TricostStrike.class.getSimpleName(), 3, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = 3;
        this.damage = this.baseDamage = 6;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (int i = 0; i < 3; i++)
            addToBot(new PummelDamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn)));

        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HEAVY));

        boolean isEliteOrBoss = (AbstractDungeon.getCurrRoom()).eliteTrigger;
        for (AbstractMonster mo : (AbstractDungeon.getMonsters()).monsters) {
            if (mo.type == AbstractMonster.EnemyType.BOSS)
                isEliteOrBoss = true;
        }
        if (isEliteOrBoss) {
            addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, this.magicNumber)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TricostStrike();
    }
}
