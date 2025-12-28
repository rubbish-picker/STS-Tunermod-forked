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

public class Draw0Power extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(Draw0Power.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";

    private boolean justApplied = true;

    private int formerDrawNum;

    public Draw0Power(AbstractCreature owner) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.DEBUFF;

        loadRegion("lessdraw");
        this.updateDescription();
    }

    @Override
    public void onInitialApplication() {
        if(AbstractDungeon.player.gameHandSize < 4)
            this.formerDrawNum = AbstractDungeon.player.gameHandSize;
        else this.formerDrawNum = 4;
        AbstractDungeon.player.gameHandSize -= this.formerDrawNum;
    }

    @Override
    public void atEndOfRound() {
        if (this.justApplied) {
            this.justApplied = false;
            return;
        }
        AbstractDungeon.player.gameHandSize += this.formerDrawNum;
        this.addToBot(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}