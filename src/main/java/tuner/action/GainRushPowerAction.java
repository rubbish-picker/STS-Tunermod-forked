package tuner.action;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.powers.RushPower;
import tuner.relics.ATRelic;

public class GainRushPowerAction extends AbstractGameAction {

    private AbstractCard c;

    public GainRushPowerAction(AbstractCard c, int amount) {
        this.c = c;
        this.amount = amount;
    }

    public GainRushPowerAction(int amount) {
        this(null, amount);
    }

    @Override
    public void update() {
        int amp = 0;
        if(c != null) {
            for (AbstractCardModifier mod : CardModifierManager.modifiers(c))
                if (mod.identifier(c).equals("tuner:SneckoOil"))
                    amp += ((AbstractMod)mod).owner.magicNumber;
        }


        addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new RushPower(AbstractDungeon.player, this.amount+amp)));
        this.tickDuration();
    }
}
