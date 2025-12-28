package tuner.patches.utils;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import tuner.characters.Tuner;
import tuner.misc.shijian.Shijian;

public class MetricsPatch {
    public static boolean death = true;
    public static boolean trueVictor = false;
    public static MonsterGroup monsters =null;

    @SpirePatch(clz = Metrics.class, method = "gatherAllDataAndSave",
    paramtypez = {boolean.class, boolean.class, MonsterGroup.class})
    public static class ShijianPatch {

        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(Metrics __instance, boolean death, boolean trueVictor, MonsterGroup monsters) {
            if (AbstractDungeon.player instanceof Tuner) {
                MetricsPatch.monsters = monsters;
                MetricsPatch.death = death;
                MetricsPatch.trueVictor = trueVictor;
                Shijian.shijian();
            }
            return SpireReturn.Continue();
        }
    }
}