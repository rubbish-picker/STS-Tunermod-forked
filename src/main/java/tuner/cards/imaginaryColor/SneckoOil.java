package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.actions.unique.RandomizeHandCostAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class SneckoOil extends MouldCard {
    public SneckoOil() {
        super(SneckoOil.class.getSimpleName(), 0, Imaginary, CardRarity.COMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 3;
        this.tags.add(CardTags.HEALING);
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new SneckoOil();
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
            upgradeMagicNumber(2);
            ModHelper.initDes(this);
        }
    }
}
