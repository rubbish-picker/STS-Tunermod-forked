package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import tuner.action.MakeATempImaginaryAction;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Worship extends MouldCard {
    public Worship() {
        super(Worship.class.getSimpleName(), 2, Imaginary, CardRarity.UNCOMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 5;
        this.tags.add(CardTags.HEALING);
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                        new MantraPower(AbstractDungeon.player, this.owner.magicNumber)));
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
        return new Worship();
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
