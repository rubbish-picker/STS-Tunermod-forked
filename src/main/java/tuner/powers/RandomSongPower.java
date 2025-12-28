package tuner.powers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.cards.AbsorptionBarrier;
import tuner.helpers.ModHelper;
import tuner.modCore.AstrographTuner;

public class RandomSongPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(RandomSongPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = ModHelper.makeRlcAd(RandomSongPower.class.getSimpleName(), true);
    private static final String PATH48 = ModHelper.makeRlcAd(RandomSongPower.class.getSimpleName(), false);


    public RandomSongPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.DEBUFF;

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(PATH128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(PATH48), 0, 0, 32, 32);

        this.updateDescription();
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @SpirePatch(clz = HealAction.class,
            method = "update")
    public static class PatchHealAction {
        @SpirePrefixPatch
        public static SpireReturn Prefix(HealAction _inst) {
            if (_inst.target != null && !_inst.target.isDeadOrEscaped() &&
                    _inst.target.hasPower(RandomSongPower.POWER_ID)) {
                AbstractDungeon.actionManager.addToTop(
                        new DamageAction(_inst.target,
                                new DamageInfo(_inst.target,
                                        _inst.target.getPower(RandomSongPower.POWER_ID).amount,
                                        DamageInfo.DamageType.THORNS),
                                AbstractGameAction.AttackEffect.POISON));
                _inst.isDone = true;
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCreature.class,
            method = "addBlock")
    public static class PatchGainBlockAction {
        @SpirePrefixPatch
        public static SpireReturn Prefix(AbstractCreature _inst, int blockAmount) {
            if (_inst.hasPower(RandomSongPower.POWER_ID) && blockAmount > 0) {
                AbstractDungeon.actionManager.addToTop(
                        new DamageAction(_inst,
                                new DamageInfo(_inst,
                                        _inst.getPower(RandomSongPower.POWER_ID).amount,
                                        DamageInfo.DamageType.THORNS),
                                AbstractGameAction.AttackEffect.POISON));
                return SpireReturn.Return();
            }

            if (_inst instanceof AbstractPlayer && blockAmount > 0) {
                boolean temp = false;
                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                    if (c instanceof AbsorptionBarrier && !((AbsorptionBarrier) c).onUse) {
                        ((AbsorptionBarrier) c).onGainBlock(blockAmount);
                        c.flash();
                        temp = true;
                    }
                }
                if (temp)
                    return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = ApplyPowerAction.class,
            method = "update")
    public static class PatchApplyPowerAction {
        @SpirePrefixPatch
        public static SpireReturn Prefix(ApplyPowerAction _inst, AbstractPower ___powerToApply) {
            if (_inst.target != null && !_inst.target.isDeadOrEscaped() &&
                    _inst.target.hasPower(RandomSongPower.POWER_ID)
                    && ___powerToApply instanceof StrengthPower && ___powerToApply.amount > 0) {
                AbstractDungeon.actionManager.addToTop(
                        new DamageAction(_inst.target,
                                new DamageInfo(_inst.target,
                                        _inst.target.getPower(RandomSongPower.POWER_ID).amount,
                                        DamageInfo.DamageType.THORNS),
                                AbstractGameAction.AttackEffect.POISON));
                _inst.isDone = true;
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}