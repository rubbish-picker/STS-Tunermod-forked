package tuner.patches.fullArt;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import tuner.interfaces.FullArtSubscriber;

import java.util.ArrayList;

public class SCVPanelPatch {

    @SpirePatch(clz = SingleCardViewPopup.class, method = "updateInput")
    public static class updateInputPatch {
        @SpirePrefixPatch
        public static void updateInputPatch(SingleCardViewPopup _inst, Hitbox ___cardHb, AbstractCard ___card) {
            if (InputHelper.justReleasedClickRight && ___cardHb.hovered) {
                FullArtSubscriber.exchangeFullArt(___card);
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class RenderPatch {
        private static ArrayList<Texture> imgs = new ArrayList<>();
        private static ArrayList<Integer> amounts = new ArrayList<>();
        private static ArrayList<Color> colors = new ArrayList<>();


        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
            if (FullArtSubscriber.initShouldShowFullArt(___card)) {
                //render number
                imgs.clear();
                amounts.clear();
                colors.clear();
                if (___card.baseDamage > 0) {
                    imgs.add(FullArtSubscriber.getAttackImage(___card.baseDamage));
                    amounts.add(___card.baseDamage);
                    colors.add(Color.RED);
                }
                if (___card.baseBlock > 0) {
                    imgs.add(FullArtSubscriber.getDefendImage());
                    amounts.add(___card.baseBlock);
                    colors.add(Settings.CREAM_COLOR);
                }
                if (___card.baseMagicNumber > 0) {
                    imgs.add(FullArtSubscriber.getMGCImage());
                    amounts.add(___card.baseMagicNumber);
                    colors.add(Settings.BLUE_TEXT_COLOR);
                }

                float numberX = 175F;
                float numberY = 200;

                if (imgs.size() == 1) numberY = 200;
                if (imgs.size() == 2) numberY = 170;
                if (imgs.size() == 3) numberY = 150;

                int i = 0;

                for (Texture img : imgs) {
                    int size = img.getWidth();
                    sb.draw(img,
                            Settings.WIDTH / 2.0F + numberX * Settings.scale - size / 2.0F, Settings.HEIGHT / 2.0F - numberY * Settings.scale - size / 2.0F,
                            size / 2.0F, size / 2.0F, size, size,
                            1.5F, 1.5F,
                            0.0F, 0, 0,
                            size, size, false, false);
                    FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, amounts.get(i).toString(),
                            Settings.WIDTH / 2.0F + (numberX + 60) * Settings.scale, Settings.HEIGHT / 2.0F - numberY * Settings.scale,
                            colors.get(i));
                    numberY += 50;
                    i++;
                }
            }
        }
    }
}
