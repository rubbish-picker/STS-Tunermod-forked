package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.Frost;
import com.megacrit.cardcrawl.orbs.Plasma;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Fuuka extends MouldCard {
    public Fuuka() {
        super(Fuuka.class.getSimpleName(), 2, Imaginary, CardRarity.RARE, CardTarget.NONE, ImaginaryColor);
        this.exhaust = true;
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
//                for()
                addToBot(new ChannelAction(new Plasma()));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fuuka();
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
            upgradeBaseCost(1);
            if (timesUpgraded == 2) {
                this.exhaust = false;
                this.rawDescription = this.DESCRIPTION_UPG;
                initializeDescription();
            }
        }
    }
}
