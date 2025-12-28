package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.interfaces.BeingRewroteSubscriber;
import tuner.interfaces.FullArtSubscriber;

public class SpottingRound extends MouldCard implements FullArtSubscriber {
    public SpottingRound() {
        super(SpottingRound.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 7;
        this.magicNumber = this.baseMagicNumber = 7;
    }

    private int count = 0;

    public void check() {
        count++;
    }

    public void init(){
        count = 0;
    }

    @Override
    public void triggerOnGlowCheck() {
        if (count == 1) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR;
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR;
        }
    }

    @Override
    public void applyPowers() {
        if (count == 1) {
            int tmp = this.baseDamage;
            this.baseDamage *= this.baseMagicNumber;
            super.applyPowers();
            this.baseDamage = tmp;
            this.isDamageModified = this.damage != this.baseDamage;
        } else super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        if (count == 1) {
            int tmp = this.baseDamage;
            this.baseDamage *= this.baseMagicNumber;
            super.calculateCardDamage(mo);
            this.baseDamage = tmp;
            this.isDamageModified = this.damage != this.baseDamage;
        } else super.calculateCardDamage(mo);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        count--;
        calculateCardDamage(m);
        count++;
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SpottingRound();
    }
}
