package tuner.cards.imaginaryColor;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Enlightenment extends MouldCard {
    public Enlightenment() {
        super(Enlightenment.class.getSimpleName(), 3, Imaginary, CardRarity.UNCOMMON, CardTarget.NONE, ImaginaryColor);
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
        };
    }

    @Override
    public void rewritingChange(AbstractCard c) {
        if (c.cost > 1)
            c.modifyCostForCombat(1 - c.cost);
    }

    @Override
    public AbstractCard makeCopy() {
        return new Enlightenment();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
        if (ModHelper.imgUpgradeName(this)) {
            if (this.cost > 0)
                upgradeBaseCost(this.cost - 1);
        }
    }
}
