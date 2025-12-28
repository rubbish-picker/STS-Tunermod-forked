package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BlurPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Blur extends MouldCard {
    public Blur() {
        super(Blur.class.getSimpleName(), 1, Imaginary, CardRarity.COMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 1;
        this.tags.add(CardTags.HEALING);
        this.exhaust = true;
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                        new BlurPower(AbstractDungeon.player, this.owner.magicNumber)));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blur();
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
            this.exhaust = false;
            if (this.timesUpgraded == 2)
                upgradeMagicNumber(1);
            this.rawDescription = String.format(this.DESCRIPTION_UPG, this.magicNumber);
            this.initializeDescription();
        }
    }
}
