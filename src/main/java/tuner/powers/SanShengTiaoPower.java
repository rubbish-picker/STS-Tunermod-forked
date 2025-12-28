package tuner.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.OmegaFlashEffect;
import tuner.helpers.ModHelper;

import java.util.HashSet;

public class SanShengTiaoPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(SanShengTiaoPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";

    private boolean justUsed = false;

    public SanShengTiaoPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.amount = amount;
        loadRegion("omega");
        this.updateDescription();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                justUsed = false;
                this.isDone = true;
            }
        });
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            justUsed = true;
        }
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (!justUsed) {
            HashSet<Integer> set = new HashSet<>();
            for (AbstractCard c : ModHelper.rtATgroup()) {
                set.add(c.costForTurn);
            }

            if (set.size() >= 3) {
                justUsed = true;
                flash();
                for (AbstractMonster m : (AbstractDungeon.getMonsters()).monsters) {
                    if (m != null && !m.isDeadOrEscaped()) {
                        if (Settings.FAST_MODE) {
                            addToBot(new VFXAction(new OmegaFlashEffect(m.hb.cX, m.hb.cY)));
                            continue;
                        }
                        addToBot(new VFXAction(new OmegaFlashEffect(m.hb.cX, m.hb.cY), 0.2F));
                    }
                }
                addToBot(new DamageAllEnemiesAction(null,
                        DamageInfo.createDamageMatrix(this.amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.FIRE, true));
            }
        }
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}