package tuner.patches.ui;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import tuner.cards.MouldCard;
import tuner.misc.PIDStrings;
import tuner.modCore.CardTypeEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SingleCardViewPopupPatch {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("tuner:ImaginaryCard");

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardTypeText")
    public static class PatchRenderCardTypeText {
        @SpireInsertPatch(rloc = 1, localvars = {"label"})
        public static void Insert(SingleCardViewPopup singleCardViewPopup, SpriteBatch sb, @ByRef String[] _label) {
            AbstractCard card = (AbstractCard) ReflectionHacks.getPrivate(singleCardViewPopup, SingleCardViewPopup.class, "card");
            if (card.type == CardTypeEnum.Imaginary)
                _label[0] = cardStrings.EXTENDED_DESCRIPTION[0];
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "render")
    public static class PatchRender {
        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup singleCardViewPopup, SpriteBatch sb, AbstractCard ___card) {

            if (___card instanceof MouldCard &&
                    PIDStrings.namePIDMap.get(___card.cardID) != null &&
                    !PIDStrings.namePIDMap.get(___card.cardID).equals("NULL")) {

                float BODY_TEXT_WIDTH = 280.0F * Settings.scale;
                float TIP_DESC_LINE_SPACING = 26.0F * Settings.scale;

                String title = "Card_PID:";
                String body = PIDStrings.namePIDMap.get(___card.cardID);

                try {
                    Field textHeight = TipHelper.class.getDeclaredField("textHeight");
                    textHeight.setAccessible(true);
                    Method renderTipBox = TipHelper.class.getDeclaredMethod("renderTipBox", new Class[] { float.class, float.class, SpriteBatch.class, String.class, String.class });
                    renderTipBox.setAccessible(true);

                    float height = -FontHelper.getSmartHeight(FontHelper.tipBodyFont, body, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
                    textHeight.setFloat(null, height - 7.0F * Settings.scale);

                    renderTipBox.invoke(null, new Object[] { Settings.WIDTH / 2.0F + 340.0F * Settings.scale, 900.0F * Settings.scale, sb, title, body });
                } catch (IllegalAccessException|NoSuchFieldException|NoSuchMethodException|java.lang.reflect.InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}