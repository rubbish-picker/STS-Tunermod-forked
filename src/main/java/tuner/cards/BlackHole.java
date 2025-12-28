package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.modCore.AstrographTuner;
import tuner.powers.RushPower;

public class BlackHole extends MouldCard {


    public BlackHole() {
        super(BlackHole.class.getSimpleName(), 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 7;
        this.magicNumber = this.baseMagicNumber = 0;
        this.tags.add(CardTags.STRIKE);
    }

    @Override
    public void atTurnStart() {
        this.magicNumber = this.baseMagicNumber = 0;
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        if (c.type == CardType.ATTACK && c != this) {
            if (this.baseMagicNumber < 4) {
                this.baseMagicNumber++;
                this.magicNumber = this.baseMagicNumber;
            }
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (this.magicNumber < 2)
            addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        else
            addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                    AbstractGameAction.AttackEffect.SLASH_HEAVY));

        if (this.magicNumber >= 1) {
            addToBot(new GainEnergyAction(this.magicNumber));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BlackHole();
    }
}
