package tuner.patches.fullArt;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import tuner.interfaces.FullArtSubscriber;

import java.lang.reflect.Field;

public class SCVFullArtRenderCardPatch {

    private static void renderHelper(SingleCardViewPopup scv, SpriteBatch sb, float x, float y, TextureAtlas.AtlasRegion img) {
        ReflectionHacks.privateMethod(SingleCardViewPopup.class, "renderHelper", new Class[]{SpriteBatch.class, float.class, float.class, TextureAtlas.AtlasRegion.class}).invoke(scv, new Object[]{sb, Float.valueOf(x), Float.valueOf(y), img});
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "<class>")
    public static class Fields {
        public static SpireField<TextureAtlas.AtlasRegion> signature = new SpireField(() -> null);
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "loadPortraitImg", paramtypez = {})
    public static class LoadPortraitImgPatch {
        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup _inst, AbstractCard ___card) {
            if (___card instanceof FullArtSubscriber) {
                Texture t = ImageMaster.loadImage(((FullArtSubscriber) ___card).getFullArtPortraitImgPath());
                SCVFullArtRenderCardPatch.Fields.signature.set(_inst, new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight()));
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "close", paramtypez = {})
    public static class ClosePatch {
        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup _inst) {
            if (SCVFullArtRenderCardPatch.Fields.signature.get(_inst) != null) {
                (SCVFullArtRenderCardPatch.Fields.signature.get(_inst)).getTexture().dispose();
                SCVFullArtRenderCardPatch.Fields.signature.set(_inst, null);
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardBack", paramtypez = {SpriteBatch.class})
    public static class RenderCardBackPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
            if (___card.isLocked)
                return SpireReturn.Continue();
            if (FullArtSubscriber.initShouldShowFullArt(___card)) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderPortrait", paramtypez = {SpriteBatch.class})
    public static class RenderPortraitPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
            if (___card.isLocked)
                return SpireReturn.Continue();
            if (FullArtSubscriber.initShouldShowFullArt(___card)) {
                SCVFullArtRenderCardPatch.renderHelper(_inst, sb, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F,
                        SCVFullArtRenderCardPatch.Fields.signature.get(_inst));
                SCVFullArtRenderCardPatch.renderHelper(_inst, sb, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F,
                        FullArtSubscriber.getCardBg());
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderFrame", paramtypez = {SpriteBatch.class})
    public static class RenderFramePatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
            if (FullArtSubscriber.initShouldShowFullArt(___card))
                return SpireReturn.Return();
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardBanner", paramtypez = {SpriteBatch.class})
    public static class RenderCardBannerPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
            if (FullArtSubscriber.initShouldShowFullArt(___card))
                return SpireReturn.Return();
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardTypeText", paramtypez = {SpriteBatch.class})
    public static class RenderCardTypeTextPatch {
        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
                return LineFinder.findInOrder(ctBehavior, (Matcher) new Matcher.MethodCallMatcher(FontHelper.class, "renderFontCentered"));
            }
        }

        @SpireInsertPatch(locator = Locator.class, localvars = {"label"})
        public static SpireReturn<Void> Insert(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card, String label) {
            if (FullArtSubscriber.initShouldShowFullArt(___card))
                return SpireReturn.Return();
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescription", paramtypez = {SpriteBatch.class})
    public static class RenderDescriptionPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
            if (FullArtSubscriber.initShouldShowFullArt(___card))
                return SpireReturn.Return();
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescriptionCN", paramtypez = {SpriteBatch.class})
    public static class RenderDescriptionCNPatch {
        @SpirePrefixPatch
        public static SpireReturn<Void> Prefix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
            if (FullArtSubscriber.initShouldShowFullArt(___card))
                return SpireReturn.Return();
            return SpireReturn.Continue();
        }
    }

//    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCost", paramtypez = {SpriteBatch.class})
//    public static class RenderCostPatch {
//        @SpirePrefixPatch
//        public static SpireReturn<Void> Prefix(SingleCardViewPopup _inst, SpriteBatch sb, AbstractCard ___card) {
//            if (___card instanceof FullArtSubscriber && FullArtSubscriber.initShouldShowFullArt(___card.cardID)) {
//                return SpireReturn.Return();
//            }
//            return SpireReturn.Continue();
//        }
//    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "render", paramtypez = {SpriteBatch.class})
    public static class RenderTitlePatch {

        public static void localRenderFontCentered(SingleCardViewPopup _inst, SpriteBatch sb) {
            try {
                Class<SingleCardViewPopup> scv = SingleCardViewPopup.class;
                Field cardField = scv.getDeclaredField("card");
                cardField.setAccessible(true);
                Object card = cardField.get(_inst);
                if (FullArtSubscriber.initShouldShowFullArt(((AbstractCard) card))) {
                    if(((AbstractCard)card).upgraded){
                        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, ((AbstractCard) card).name,
                                Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F - 200.0F * Settings.scale,
                                Settings.GREEN_TEXT_COLOR);
                    }else{
                        FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, ((AbstractCard) card).name,
                                Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F - 200.0F * Settings.scale,
                                Settings.CREAM_COLOR);
                    }
                } else {
                    ReflectionHacks.privateMethod(SingleCardViewPopup.class, "renderTitle",
                            SpriteBatch.class).invoke(_inst, new Object[]{sb});
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderTitle")) {
                        m.replace(String.format(
                                "%s.localRenderFontCentered($0, $1);",
                                RenderTitlePatch.class.getName()
                        ));
                    }
                }
            };
        }
    }
}

