package tuner.patches.combat;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import tuner.helpers.ModHelper;

@SpirePatch(clz = UseCardAction.class, method = "update")
public class CardPlayedPatch {
    //闪烁等牌
    @SpireInstrumentPatch
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("moveToDiscardPile"))
                    m.replace(String.format("{ if (%s.onUsedCard($1)) { $_ = $proceed($$); } }", new Object[] { ModHelper.class.getName() }));
            }
        };
    }
}