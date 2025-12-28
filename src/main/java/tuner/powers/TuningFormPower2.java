package tuner.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.helpers.ModHelper;

public class TuningFormPower2 extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(TuningFormPower2.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";

    private int damaged = 0;

    public TuningFormPower2(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = amount;
        loadRegion("flight");
        this.updateDescription();
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * 0.75F;
        }
        return damage;
    }

    @Override
    public void wasHPLost(DamageInfo info, int damageAmount) {
        if (info.type == DamageInfo.DamageType.NORMAL) {
            this.damaged++;
        }
    }

    @Override
    public void atEndOfRound() {
        if (this.damaged > 0) {
            addToTop(new ReducePowerAction(this.owner, this.owner, this, this.damaged));
            this.damaged = 0;
        }
    }

    @Override
    public void atStartOfTurn() {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (TuningFormPower2.this.owner.hasPower(TuningFormPower2.this.ID)) {
                    TuningFormPower2.this.amount++;
                    addToTop(new GainEnergyAction(TuningFormPower2.this.amount));
                }
                this.isDone = true;
            }
        });
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}