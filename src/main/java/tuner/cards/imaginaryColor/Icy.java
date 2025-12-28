package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Chill;
import com.megacrit.cardcrawl.cards.blue.Defend_Blue;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Frost;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Icy extends MouldCard {
    public Icy() {
        super(Icy.class.getSimpleName(), 1, Imaginary, CardRarity.UNCOMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 1;
        this.tags.add(CardTags.HEALING);
        this.exhaust = true;
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                for (int i = 0; i < this.owner.magicNumber; i++)
                    addToBot(new ChannelAction(new Frost()));
                if (this.owner.timesUpgraded == 2) {
                    addToBot(new IncreaseMaxOrbAction(1));
                }
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Icy();
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
            if (this.timesUpgraded == 1) {
                this.exhaust = false;
                this.rawDescription = this.DESCRIPTION_UPG;
                initializeDescription();
            }
            if (this.timesUpgraded == 2) {
                this.rawDescription = this.cardStrings.EXTENDED_DESCRIPTION[0];
                initializeDescription();
            }
        }
    }
}
