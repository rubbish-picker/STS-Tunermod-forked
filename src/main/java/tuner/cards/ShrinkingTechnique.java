package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import tuner.action.GainRushPowerAction;
import tuner.powers.JiabanPower;
import tuner.powers.RushPower;

public class ShrinkingTechnique extends MouldCard {

    public boolean onUse = false;

    public ShrinkingTechnique() {
        super(ShrinkingTechnique.class.getSimpleName(), 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 8;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainRushPowerAction(this, this.magicNumber));
        if (this.upgraded)
            addToBot(new ApplyPowerAction(p, p, (new JiabanPower(p, 1)), 0));
        else
            addToBot(new ApplyPowerAction(p, p, (new JiabanPower(p, 2)), 0));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = this.DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ShrinkingTechnique();
    }
}
