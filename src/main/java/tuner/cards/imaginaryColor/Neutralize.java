package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Neutralize extends MouldCard {
    public Neutralize() {
        super(Neutralize.class.getSimpleName(), 0, Imaginary, CardRarity.COMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 1;
        ModHelper.initDes(this);
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                target = ModHelper.rtTarget(target);
                if (target != null)
                    addToBot(new ApplyPowerAction(target, AbstractDungeon.player, new WeakPower(target, this.owner.magicNumber, false)));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Neutralize();
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
            ModHelper.initDes(this);
        }
    }
}
