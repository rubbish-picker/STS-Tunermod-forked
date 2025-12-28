package tuner.powers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.helpers.ModHelper;

import java.lang.reflect.Field;

public class SleepPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(SleepPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = ModHelper.makeRlcAd(SleepPower.class.getSimpleName(), true);
    private static final String PATH48 = ModHelper.makeRlcAd(SleepPower.class.getSimpleName(), false);

    private byte moveByte;
    private AbstractMonster.Intent moveIntent;
    private EnemyMoveInfo move;

    public SleepPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(PATH128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(PATH48), 0, 0, 32, 32);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount;
        if (this.amount == 1) {
            this.description = this.description + DESCRIPTIONS[1];
        } else {
            this.description = this.description + DESCRIPTIONS[2];
        }
    }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        return type != DamageInfo.DamageType.HP_LOSS && type != DamageInfo.DamageType.THORNS ? damage / 2.0F : damage;
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        Boolean willLive = this.calculateDamageTakenAmount((float) damageAmount, info.type) < (float) this.owner.currentHealth;
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && willLive) {
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }

        return damageAmount;
    }


    @Override
    public void atEndOfRound() {
        if (this.amount <= 0) {
            AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        } else {
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this.owner, this.owner, this, 1));
        }
    }

    @Override
    public void onInitialApplication() {
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            public void update() {
                if (SleepPower.this.owner instanceof AbstractMonster) {
                    SleepPower.this.moveByte = ((AbstractMonster) SleepPower.this.owner).nextMove;
                    SleepPower.this.moveIntent = ((AbstractMonster) SleepPower.this.owner).intent;

                    try {
                        Field f = AbstractMonster.class.getDeclaredField("move");
                        f.setAccessible(true);
                        SleepPower.this.move = (EnemyMoveInfo) f.get(SleepPower.this.owner);
                        EnemyMoveInfo stunMove = new EnemyMoveInfo(SleepPower.this.moveByte, AbstractMonster.Intent.SLEEP, -1, 0, false);
                        f.set(SleepPower.this.owner, stunMove);
                        ((AbstractMonster) SleepPower.this.owner).createIntent();
                    } catch (NoSuchFieldException | IllegalAccessException var3) {
                        var3.printStackTrace();
                    }
                }

                this.isDone = true;
            }
        });
    }

    @Override
    public void onRemove() {
        if (this.owner instanceof AbstractMonster) {
            AbstractMonster m = (AbstractMonster) this.owner;
            if (this.move != null) {
                m.setMove(this.moveByte, this.moveIntent, this.move.baseDamage, this.move.multiplier, this.move.isMultiDamage);
            } else {
                m.setMove(this.moveByte, this.moveIntent);
            }

            m.createIntent();
            m.applyPowers();
        }

    }
}