package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.effects.AnimatedSlashEffect;
import tuner.effects.FlyingBigDaggerEffect;
import tuner.effects.GunFireEffect;
import tuner.powers.RushPower;

public class LongShot extends MouldCard {
    public LongShot() {
        super(LongShot.class.getSimpleName(), 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 13;
        this.magicNumber = this.baseMagicNumber = 2;
    }

    @Override
    public void applyPowers() {
        int realBaseDamage = this.baseDamage;
        if (AbstractDungeon.player.hasPower(RushPower.POWER_ID)) {
            this.baseDamage += AbstractDungeon.player.getPower(RushPower.POWER_ID).amount * this.magicNumber;
        }

        super.applyPowers();

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int realBaseDamage = this.baseDamage;
        if (AbstractDungeon.player.hasPower(RushPower.POWER_ID)) {
            this.baseDamage += AbstractDungeon.player.getPower(RushPower.POWER_ID).amount * this.magicNumber;
        }

        super.calculateCardDamage(mo);

        this.baseDamage = realBaseDamage;
        this.isDamageModified = (this.damage != this.baseDamage);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new WaitAction(0.1F));
        addToBot(new WaitAction(0.1F));
        addToBot(new WaitAction(0.1F));
        addToBot(new VFXAction(new FlyingBigDaggerEffect(
                p.hb.cX + 100 * Settings.scale, p.hb.cY - 100 * Settings.scale, p.flipHorizontal)));
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            upgradeDamage(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new LongShot();
    }
}
