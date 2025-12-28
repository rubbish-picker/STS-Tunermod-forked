package tuner.patches.fullArt;

import tuner.effects.FullArtExchangeEffect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

public class AbstractCardPatch {
    private static float target_x = 0.0F;

    private static float target_y = 0.0F;

    private static float current_x = 0.0F;

    private static float current_y = 0.0F;

    private static float angle = 0.0F;

    private static float targetAngle = 0.0F;

    public static float cardScaleX = 1.0F;

    public static FrameBuffer cardBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);

    public static Texture cardImage = null;

    @SpirePatch(clz = AbstractCard.class, method = "renderCard")
    public static class FlipCardEffectPatches1 {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(AbstractCard _instance, SpriteBatch sb, boolean hovered, boolean selected) {

            AbstractCardPatch.cardScaleX = FullArtExchangeEffect.shouldExchange(_instance.cardID);

            if (AbstractCardPatch.cardScaleX < 1.0F) {
                AbstractCardPatch.target_x = _instance.target_x;
                AbstractCardPatch.target_y = _instance.target_y;
                AbstractCardPatch.current_x = _instance.current_x;
                AbstractCardPatch.current_y = _instance.current_y;
                AbstractCardPatch.targetAngle = _instance.targetAngle;
                AbstractCardPatch.angle = _instance.angle;
                _instance.target_x = Settings.WIDTH / 2.0F;
                _instance.target_y = Settings.HEIGHT / 2.0F;
                _instance.current_x = Settings.WIDTH / 2.0F;
                _instance.current_y = Settings.HEIGHT / 2.0F;
                _instance.setAngle(0.0F, true);
                sb.end();
                AbstractCardPatch.cardBuffer.begin();
                Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                Gdx.gl.glClear(16640);
                Gdx.gl.glColorMask(true, true, true, true);
                sb.begin();
                sb.setColor(Color.WHITE);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderCard")
    public static class FlipCardEffectPatches2 {
        @SpirePostfixPatch
        public static SpireReturn<Void> Postfix(AbstractCard _instance, SpriteBatch sb, boolean hovered, boolean selected) {
            if (AbstractCardPatch.cardScaleX < 1.0F) {
                sb.end();
                AbstractCardPatch.cardBuffer.end();
                AbstractCardPatch.cardImage = AbstractCardPatch.cardBuffer.getColorBufferTexture();
                sb.begin();
                _instance.target_x = AbstractCardPatch.target_x;
                _instance.target_y = AbstractCardPatch.target_y;
                _instance.current_x = AbstractCardPatch.current_x;
                _instance.current_y = AbstractCardPatch.current_y;
                _instance.targetAngle = AbstractCardPatch.targetAngle;
                _instance.angle = AbstractCardPatch.angle;

                sb.draw(AbstractCardPatch.cardImage,
                        AbstractCardPatch.current_x - AbstractCardPatch.cardImage.getWidth() / 2.0F,
                        AbstractCardPatch.current_y - AbstractCardPatch.cardImage.getHeight() / 2.0F,
                        AbstractCardPatch.cardImage.getWidth() / 2.0F, AbstractCardPatch.cardImage.getHeight() / 2.0F,
                        AbstractCardPatch.cardImage.getWidth(), AbstractCardPatch.cardImage.getHeight(),
                        AbstractCardPatch.cardScaleX, 1.0F,
                        AbstractCardPatch.angle,
                        0, 0,
                        AbstractCardPatch.cardImage.getWidth(), AbstractCardPatch.cardImage.getHeight(),
                        false, true);
            }
            return SpireReturn.Continue();
        }
    }
}
