package tuner.patches.powers;

//import tuner.powers.MindControlPower;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import tuner.powers.MindControlPower;

@SpirePatch(clz = DamageAction.class, method = "update")
public class MindControlRetargetPatch {

    public static boolean isAlive() {
        int flag = 1;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped()) {
                flag--;
            }
            if (flag < 0) return true;
        }
        return false;
    }

    @SpireInsertPatch(locator = Locator.class, localvars = {"info"})
    public static void ChangeTarget(DamageAction instance, @ByRef DamageInfo[] info) {
        if (instance.source != null &&
                instance.source.hasPower(MindControlPower.POWER_ID) &&
                isAlive()) {
            instance.target = AbstractDungeon.getMonsters().getRandomMonster((AbstractMonster) instance.source, true, AbstractDungeon.cardRandomRng);
            if (instance.target != null)
                info[0].applyPowers(instance.source, instance.target);
        }
    }

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
            return LineFinder.findInOrder(ctMethodToPatch, (Matcher) methodCallMatcher);
        }
    }

    @SpirePatch(clz = VampireDamageAction.class, method = "update")
    public static class VampireDamageActionRetarget {
        @SpireInsertPatch(locator = Locator.class, localvars = {"info"})
        public static void ChangeTarget(VampireDamageAction instance, @ByRef DamageInfo[] info) {
            if (instance.source != null &&
                    instance.source.hasPower(MindControlPower.POWER_ID) &&
                    isAlive()) {
                instance.target = AbstractDungeon.getMonsters().getRandomMonster((AbstractMonster) instance.source, true, AbstractDungeon.cardRandomRng);
                if (instance.target != null)
                    info[0].applyPowers(instance.source, instance.target);
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, (Matcher) methodCallMatcher);
            }
        }
    }
}