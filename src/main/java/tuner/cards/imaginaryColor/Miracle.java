package tuner.cards.imaginaryColor;

import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

@NoCompendium
public class Miracle extends MouldCard {
    public Miracle() {
        super(Miracle.class.getSimpleName(), 0, Imaginary, CardRarity.SPECIAL, CardTarget.NONE, ImaginaryColor);
        this.steal(new com.megacrit.cardcrawl.cards.tempCards.Miracle());
        this.magicNumber = this.baseMagicNumber = 1;
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                addToBot(new GainEnergyAction(this.owner.baseMagicNumber));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Miracle();
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
        if(ModHelper.imgUpgradeName(this)){
            upgradeMagicNumber(1);
            if(this.timesUpgraded == 1) this.rawDescription = DESCRIPTION_UPG;
            if(this.timesUpgraded == 2) this.rawDescription = EXTENDED_DESCRIPTION[0];
            initializeDescription();
        }
    }
}
