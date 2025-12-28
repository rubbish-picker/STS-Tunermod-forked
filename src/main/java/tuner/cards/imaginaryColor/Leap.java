package tuner.cards.imaginaryColor;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Leap extends MouldCard {
    public Leap() {
        super(Leap.class.getSimpleName(), 2, Imaginary, CardRarity.COMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 7;
        this.tags.add(CardTags.HEALING);
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Leap();
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
            if (this.timesUpgraded == 1)
                upgradeBaseCost(this.cost - 1);
            if (this.timesUpgraded == 2) {
                upgradeMagicNumber(4);
                ModHelper.initDes(this);
            }
        }
    }
}
