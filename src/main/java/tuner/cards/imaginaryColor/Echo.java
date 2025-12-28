package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import basemod.cardmods.EtherealMod;
import basemod.cardmods.ExhaustMod;
import basemod.cardmods.RetainMod;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.EchoForm;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Echo extends MouldCard {
    public Echo() {
        super(Echo.class.getSimpleName(), 1, Imaginary, CardRarity.RARE, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 1;
        this.exhaust = true;
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                AbstractCard c = card.makeStatEquivalentCopy();
                CardModifierManager.addModifier(c, new ExhaustMod());
                CardModifierManager.addModifier(c, new EtherealMod());
                addToBot(new MakeTempCardInHandAction(c, this.owner.magicNumber));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Echo();
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

            if (this.timesUpgraded == 2) {
                upgradeMagicNumber(1);
            }
            this.rawDescription = String.format(this.DESCRIPTION_UPG, this.magicNumber);
            this.initializeDescription();
        }
    }
}
