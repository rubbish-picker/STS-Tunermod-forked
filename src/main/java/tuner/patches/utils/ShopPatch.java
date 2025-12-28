package tuner.patches.utils;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import tuner.helpers.ModHelper;
import tuner.helpers.RandomImgCard;
import tuner.relics.ATRelic;

import java.util.ArrayList;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class ShopPatch {
    public static float scale = 1;

    public static AbstractCard getRandomCard() {
        ModHelper.initSelfRamdom();

        AbstractCard c = CardLibrary.getCard(RandomImgCard.getRandomCard(3, 3, 2).id).makeCopy();

        c.price = (int) (AbstractCard.getPrice(c.rarity) * AbstractDungeon.merchantRng.random(0.9F, 1.1F));
        return c;
    }

    @SpirePatch(clz = ShopScreen.class, method = "init")
    public static class ShopBuyImagInitPatch {
        @SpirePrefixPatch
        public static SpireReturn<?> prefix(ShopScreen _inst, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards) {
            if (!AbstractDungeon.player.hasRelic(ATRelic.ID)) return SpireReturn.Continue();

            if (colorlessCards.stream().noneMatch(c -> c.color == ImaginaryColor)) {
                colorlessCards.add(0, ShopPatch.getRandomCard());
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "setStartingCardPositions")
    public static class ShopBuyImagPatch {
        private static final float DRAW_START_X = (float) Settings.WIDTH * 0.11F;

        @SpirePostfixPatch
        public static SpireReturn Postfix(ShopScreen _inst) {
            if (!AbstractDungeon.player.hasRelic(ATRelic.ID)) return SpireReturn.Continue();

            float num = _inst.colorlessCards.size() - 2 + 5;

            ShopPatch.scale = 5 / num;

            float IMG_WIDTH = AbstractCard.IMG_WIDTH_S / 5F * num;

            int tmp = (int) (((float) Settings.WIDTH - DRAW_START_X * 2.0F - IMG_WIDTH * num) / (num - 1));
            float padX = (float) ((int) ((float) tmp + IMG_WIDTH)) + 10.0F * Settings.scale * ShopPatch.scale;


            for (int i = 0; i < _inst.colorlessCards.size(); ++i) {
                _inst.colorlessCards.get(i).drawScale = num / 5F;
                _inst.colorlessCards.get(i).updateHoverLogic();
                _inst.colorlessCards.get(i).targetDrawScale = 0.75F;
                _inst.colorlessCards.get(i).current_x = DRAW_START_X + AbstractCard.IMG_WIDTH_S / 2.0F + padX * (float) i;
                _inst.colorlessCards.get(i).target_x = DRAW_START_X + AbstractCard.IMG_WIDTH_S / 2.0F + padX * (float) i;
                _inst.colorlessCards.get(i).target_y = 9999.0F * Settings.scale;
                _inst.colorlessCards.get(i).current_y = 9999.0F * Settings.scale;
            }


            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "updateCards")
    public static class ChangeIMGCardScalePatch {

        @SpirePostfixPatch
        public static SpireReturn Postfix(ShopScreen _inst) {
            if (!AbstractDungeon.player.hasRelic(ATRelic.ID)) return SpireReturn.Continue();

            for (AbstractCard c : _inst.colorlessCards) {
                if (c.hb.hovered)
                    c.targetDrawScale = 1;
                else
                    c.targetDrawScale = ShopPatch.scale * 0.75F;
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "purchaseCard")
    public static class BuyImagWithShushuPatch {

        public static AbstractCard cardPurchased;

        public static boolean hasRelic() {
            return !(cardPurchased == null || cardPurchased.color == ImaginaryColor);
        }

        @SpirePrefixPatch
        public static SpireReturn Prefix(ShopScreen _inst, AbstractCard hoveredCard) {
            cardPurchased = hoveredCard;
            return SpireReturn.Continue();
        }

        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("hasRelic"))
                        m.replace(String.format("{$_ = $proceed($$) && %s.hasRelic();}", BuyImagWithShushuPatch.class.getName()));
                }
            };
        }
    }
}
