package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RemoveAllBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;
import tuner.powers.RushPower;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Melter extends MouldCard {
    public Melter() {
        super(Melter.class.getSimpleName(), 0, Imaginary, CardRarity.UNCOMMON, CardTarget.NONE, ImaginaryColor);
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                target = ModHelper.rtTarget(target);
                if (target != null) {
                    this.addToTop(new RemoveAllBlockAction(target, AbstractDungeon.player));
                    if (this.owner.timesUpgraded == 1) {
                        this.addToTop(new RemoveSpecificPowerAction(target, target, "Metallicize"));
                        this.addToTop(new RemoveSpecificPowerAction(target, target, "Plated Armor"));
                    }
                    if (this.owner.timesUpgraded == 2)
                        this.addToTop(new RemoveSpecificPowerAction(target, target, "Artifact"));
                }
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Melter();
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
            if (this.timesUpgraded == 1) this.rawDescription = this.DESCRIPTION_UPG;
            else this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
            initializeDescription();
        }
    }
}
