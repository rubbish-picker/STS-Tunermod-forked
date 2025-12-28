package tuner.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.helpers.ModHelper;

public class MindControlPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(MindControlPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";

    private boolean justApplied = true;

    public MindControlPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;
        this.amount = amount;
        this.loadRegion("confusion");
        this.updateDescription();
    }

    @Override
    public void atEndOfRound() {
//        if (this.justApplied) {
//            this.justApplied = false;
//            return;
//        }
        if (this.amount == 0) {
            this.addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
        } else {
            addToBot(new ReducePowerAction(this.owner, this.owner, POWER_ID, 1));
        }
    }

    public void updateDescription() {
        if(this.amount == 1) {
            this.description = DESCRIPTIONS[0];
        }else{
            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[0];
        }
    }
}