package tuner.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.powers.HighDimensionalExistencePower;
import tuner.powers.JiabanPower;
import tuner.powers.RushPower;

public class AbstractMonsterPatch {
    @SpirePatch(clz = AbstractMonster.class,
            method = "damage")
    public static class YanZouDeXinQing {

        @SpirePrefixPatch
        public static SpireReturn Prefix(AbstractMonster _inst, DamageInfo info) {
            if((info.type == DamageInfo.DamageType.THORNS || info.type == DamageInfo.DamageType.HP_LOSS) &&
                    AbstractDungeon.player.hasPower(HighDimensionalExistencePower.POWER_ID) &&
                    AbstractDungeon.player.hasPower(RushPower.POWER_ID) &&
                    !AbstractDungeon.player.hasPower(JiabanPower.POWER_ID)){
                info.output += ((RushPower)AbstractDungeon.player.getPower(RushPower.POWER_ID)).additionalDamage();
            }
            return SpireReturn.Continue();
        }
    }
}