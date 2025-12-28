package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.MakeATempImaginaryAction;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Acrobatics extends MouldCard {
    public Acrobatics() {
        super(Acrobatics.class.getSimpleName(), 1, Imaginary, CardRarity.COMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 2;
        this.tags.add(CardTags.HEALING);
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                this.addToBot(new DrawCardAction(AbstractDungeon.player, this.owner.magicNumber));
                this.addToBot(new DiscardAction(AbstractDungeon.player, AbstractDungeon.player, 1, false));
            }

            @Override
            public boolean removeOnCardPlayed(AbstractCard card) {
                addToBot(new MakeATempImaginaryAction(this.owner.makeStatEquivalentCopy()));
                return true;
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Acrobatics();
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
