package tuner.patches.cards;

import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.cards.MouldCard;
import tuner.helpers.ModHelper;
import tuner.modCore.AstrographTuner;
import tuner.modCore.CardTypeEnum;
import javassist.CtBehavior;

import java.lang.reflect.InvocationTargetException;

import static tuner.cards.Mapping.qianghuaTurn;

public class AbstractCardPatch {
    public static final Color BLUE_BORDER_GLOW_COLOR = new Color(0.2F, 0.9F, 1.0F, 0.25F);


    @SpirePatch(clz = AbstractCard.class, method = "renderType")
    public static class PatchRenderType {
        private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings("tuner:ImaginaryCard");

        @SpireInsertPatch(locator = Locator.class, localvars = {"text"})
        public static void Insert(AbstractCard card, SpriteBatch sb, @ByRef String[] _text) {
            if (card.type == CardTypeEnum.Imaginary)
                _text[0] = cardStrings.EXTENDED_DESCRIPTION[0];
        }

        private static final class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher fieldAccessMatcher = new Matcher.FieldAccessMatcher(FontHelper.class, "cardTypeFont");
                return LineFinder.findInOrder(ctBehavior, (Matcher) fieldAccessMatcher);
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderPortraitFrame")
    public static class PatchRenderPortraitFrame {
        @SpireInsertPatch(rloc = 2, localvars = {"tWidth", "tOffset"})
        public static void Insert(AbstractCard card, SpriteBatch sb, float x, float y, @ByRef float[] _tWidth, @ByRef float[] _tOffset) {
            if (card.type == CardTypeEnum.Imaginary) {
                try {
                    ReflectionHacks.getCachedMethod(AbstractCard.class, "renderSkillPortrait", new Class[]{SpriteBatch.class, float.class, float.class}).invoke(card, new Object[]{sb, Float.valueOf(x), Float.valueOf(y)});
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                _tWidth[0] = AbstractCard.typeWidthSkill;
                _tOffset[0] = AbstractCard.typeOffsetSkill;
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderCardBg")
    public static class PatchRenderCardBg {
        public static SpireReturn<Void> Prefix(AbstractCard card, SpriteBatch sb, float x, float y) {
            if (card.type == CardTypeEnum.Imaginary)
                try {
                    ReflectionHacks.getCachedMethod(AbstractCard.class, "renderSkillBg", new Class[]{SpriteBatch.class, float.class, float.class}).invoke(card, new Object[]{sb, Float.valueOf(x), Float.valueOf(y)});
                    return SpireReturn.Return(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderSkillBg")
    public static class PatchRenderSkillBg {
        public static final Texture skillBg = new Texture("tunerResources/img/512/bg_skill_img.png");

        public static void Postfix(AbstractCard card, SpriteBatch sb, float x, float y) {
            if (card.type == CardTypeEnum.Imaginary)
                try {
                    ReflectionHacks.getCachedMethod(AbstractCard.class, "renderHelper", new Class[]{SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class}).invoke(card, new Object[]{sb, ReflectionHacks.getPrivate(card, AbstractCard.class, "renderColor"), new TextureAtlas.AtlasRegion(skillBg, 0, 0, skillBg.getWidth(), skillBg.getHeight()), Float.valueOf(x), Float.valueOf(y)});
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "getCardBgAtlas")
    public static class PatchGetCardBgAtlas {
        public static SpireReturn<TextureAtlas.AtlasRegion> Prefix(AbstractCard card) {
            if (card.type == CardTypeEnum.Imaginary)
                return SpireReturn.Return(ImageMaster.CARD_SKILL_BG_SILHOUETTE);
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "triggerOnGlowCheck")
    public static class PatchTriggerOnGlowCheckR {
        public static SpireReturn Postfix(AbstractCard card) {

            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && AstrographTuner.TurnCounter <= qianghuaTurn && AstrographTuner.qianghua.get(card.cardID) != null) {
                card.glowColor = Color.RED.cpy();
            } else {
                card.glowColor = BLUE_BORDER_GLOW_COLOR.cpy();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = AbstractCard.class,
            method = "render",
            paramtypez = {SpriteBatch.class})
    public static class PatchRenderImaginary {

        private static Matrix4 mx4 = new Matrix4();
        private static final Matrix4 rotatedTextMatrix = new Matrix4();

        public static SpireReturn Postfix(AbstractCard card, SpriteBatch sb) {
            if (CardModifierPatches.CardModifierFields.cardModifiers != null) {
                float x = -157.0F,
                        y = 182.0F;

                int maxAmount = 6;
                for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                    if (maxAmount < 0) break;
                    String str = mod.identifier(card);
                    if (str.contains("tuner:")) {
                        maxAmount--;
                        x += 43.0F;
                        if (maxAmount < 0) {
                            FontHelper.renderRotatedText(sb, FontHelper.cardTitleFont, "+", card.current_x, card.current_y,
                                    (x + 30F) * card.drawScale * Settings.scale, (y + 30F) * card.drawScale * Settings.scale,
                                    card.angle, false, Color.PINK);
                        } else {
                            Texture t = ((MouldCard) CardLibrary.getCard(str)).tinyPic;
                            ModHelper.renderRotateTexture(sb, t, card.current_x, card.current_y,
                                    x * Settings.scale * card.drawScale, y * Settings.scale * card.drawScale,
                                    card.drawScale * 0.3F, card.angle);
                        }
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}