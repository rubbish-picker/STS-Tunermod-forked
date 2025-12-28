package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.cards.green.WellLaidPlans;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import tuner.action.MakeATempImaginaryAction;
import tuner.cards.BreakingThroughTheArmy;
import tuner.cards.MouldCard;
import tuner.cards.Tracer;
import tuner.cards.Twinkle;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class WellPlan extends MouldCard {
    public WellPlan() {
        super(WellPlan.class.getSimpleName(), 2, Imaginary, CardRarity.UNCOMMON, CardTarget.NONE, ImaginaryColor);
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
        };
    }

    @Override
    public void rewritingChange(AbstractCard c) {
//        if (!(c instanceof Tracer || c instanceof BreakingThroughTheArmy || c instanceof Twinkle))
        c.shuffleBackIntoDrawPile = true;
    }

    @Override
    public AbstractCard makeCopy() {
        return new WellPlan();
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
            if (this.cost > 0)
                upgradeBaseCost(this.cost - 1);
        }
    }
}
