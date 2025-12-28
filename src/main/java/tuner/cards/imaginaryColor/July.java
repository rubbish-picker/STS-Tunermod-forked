package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.BerserkPower;
import com.megacrit.cardcrawl.powers.DemonFormPower;
import com.megacrit.cardcrawl.powers.DrawPower;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class July extends MouldCard {
    public July() {
        super(July.class.getSimpleName(), 3, Imaginary, CardRarity.RARE, CardTarget.NONE, ImaginaryColor);
        this.exhaust = true;
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DemonFormPower(AbstractDungeon.player, 2)));
                if (this.owner.timesUpgraded > 0)
                    addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new DrawPower(AbstractDungeon.player, 1)));
                if (this.owner.timesUpgraded > 1)
                    addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new BerserkPower(AbstractDungeon.player, 1)));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new July();
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
            if(this.timesUpgraded == 1)
                this.rawDescription = this.DESCRIPTION_UPG;
            if(this.timesUpgraded == 2)
                this.rawDescription = this.cardStrings.EXTENDED_DESCRIPTION[0];
            initializeDescription();
        }
    }
}
