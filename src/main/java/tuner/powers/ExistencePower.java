package tuner.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.action.MakeATempImaginaryAction;
import tuner.cards.imaginaryColor.Deflect;
import tuner.helpers.ModHelper;

public class ExistencePower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(ExistencePower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";

    private boolean justApplied = true;

    public ExistencePower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = amount;
        loadRegion("closeUp");
        this.updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            for (int i = 0; i < this.amount; i++)
                addToBot(new MakeATempImaginaryAction(new Deflect()));
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}