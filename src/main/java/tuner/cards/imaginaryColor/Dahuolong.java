package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
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

public class Dahuolong extends MouldCard {
    public Dahuolong() {
        super(Dahuolong.class.getSimpleName(), 2, Imaginary, CardRarity.RARE, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 0;
        this.tags.add(CardTags.HEALING);
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                addToBot(new DrawCardAction(Dahuolong.this.magicNumber));
                addToBot(new GainEnergyAction(1));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Dahuolong();
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
        if(ModHelper.imgUpgradeName(this)) {
            upgradeMagicNumber(1);
            ModHelper.initDes(this);
        }
    }
}
