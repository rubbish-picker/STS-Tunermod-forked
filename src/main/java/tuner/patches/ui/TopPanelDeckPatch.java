package tuner.patches.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import tuner.modCore.CardColorEnum;

@SuppressWarnings("unused")
public class TopPanelDeckPatch {
    private static final Color IMAGINARY_COLOR = Settings.PURPLE_COLOR.cpy();

    public static boolean hasImaginaryCard() {
        return AbstractDungeon.player.masterDeck.group.stream()
                .anyMatch(c -> c.color == CardColorEnum.ImaginaryColor);
    }

    public static int getImaginaryCardCount() {
        return (int) AbstractDungeon.player.masterDeck.group.stream()
                .filter(c -> c.color == CardColorEnum.ImaginaryColor)
                .count();
    }

    public static String getNormalCardCountStr(String arg) {
        // check if the string is already a number
        int num;
        try {
            num = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            num = -1;
        }

        if (num == -1)
            num = AbstractDungeon.player.masterDeck.size();

        num -= getImaginaryCardCount();

        return Integer.toString(num);
    }

    public static void renderImaginaryCount(SpriteBatch sb) {
        float deck_x = ReflectionHacks.getPrivateStatic(TopPanel.class, "DECK_X");
        float icon_y = ReflectionHacks.getPrivateStatic(TopPanel.class, "ICON_Y");

        FontHelper.renderFontRightTopAligned(
                sb,
                FontHelper.topPanelAmountFont,
                Integer.toString(getImaginaryCardCount()),
                deck_x + 58.0F * Settings.scale,
                icon_y + 50.0F * Settings.scale,
                IMAGINARY_COLOR
        );
    }

    @SpirePatch(clz = TopPanel.class, method = "renderTopRightIcons", paramtypez = {SpriteBatch.class})
    public static class RenderTopRightIconsPatch {

        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderFontRightTopAligned")) {
                        m.replace("{ if (" + TopPanelDeckPatch.class.getName() +
                                ".hasImaginaryCard()) { $3 = " + TopPanelDeckPatch.class.getName() +
                                ".getNormalCardCountStr($3); " + TopPanelDeckPatch.class.getName() +
                                ".renderImaginaryCount($1); } $_ = $proceed($$); }");
                    }
                }
            };
        }
    }
}
