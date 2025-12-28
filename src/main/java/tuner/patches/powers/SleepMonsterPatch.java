package tuner.patches.powers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class SleepMonsterPatch {
    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class GetNextAction {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals("com.megacrit.cardcrawl.monsters.AbstractMonster") && m
                            .getMethodName().equals("takeTurn"))
                        m.replace("if (!m.hasPower(\"tuner:SleepPower\")) {$_ = $proceed($$);}");
                }
            };
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "rollMove")
    public static class RollMove {
        public static SpireReturn<Void> Prefix(AbstractMonster __instance) {
            if (__instance.hasPower("tuner:SleepPower"))
                return SpireReturn.Return(null);
            return SpireReturn.Continue();
        }
    }
}