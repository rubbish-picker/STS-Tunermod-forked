package tuner.cards.imaginaryColor;

import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
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

@NoCompendium
public class Deflect extends MouldCard {
    public Deflect() {
        super(Deflect.class.getSimpleName(), 0, Imaginary, CardRarity.SPECIAL, CardTarget.NONE, ImaginaryColor);
        this.steal(new com.megacrit.cardcrawl.cards.green.Deflect());
        this.tags.add(CardTags.HEALING);
        this.magicNumber = this.baseMagicNumber = 4;
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, this.owner.baseMagicNumber));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Deflect();
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
