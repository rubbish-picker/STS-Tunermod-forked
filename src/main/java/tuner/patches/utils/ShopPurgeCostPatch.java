package tuner.patches.utils;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.shop.ShopScreen;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

public class ShopPurgeCostPatch {

    @SpirePatch(clz = ShopScreen.class, method = "init")
    public static class InitPatch {
        @SpirePostfixPatch
        public static void Postfix(ShopScreen _inst, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards) {
            _inst.actualPurgeCost = 0;
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "updatePurge")
    public static class UpdatePurgePatch {
        @SpirePrefixPatch
        public static void Prefix(ShopScreen _inst) {
            // 确保点击判定时价格为0，这样即使玩家没钱也能进入扣血逻辑
            _inst.actualPurgeCost = 0;
        }

        @SpirePostfixPatch
        public static void Postfix(ShopScreen _inst) {
            // 原版在删牌完成后会把 purgeAvailable 设为 false（同一商店只能删一次）
            // 这里强制保持为 true，实现“无限删牌”
            _inst.purgeAvailable = true;
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "purgeCard")
    public static class PurgeCardPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("loseGold")) {
                        // purgeCard() 才是实际执行“删牌并扣费”的地方；这里把扣金币替换成扣血
                        m.replace("{" +
                                // 扣除最大生命值：默认按当前最大生命值的一半计算，至少1点，并且不允许将最大生命值降到1以下
                                "   int maxHpCost = Math.max(1, com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.maxHealth / 2);" +
                                "   int cappedCost = Math.min(maxHpCost, com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.maxHealth - 1);" +
                                "   if (cappedCost > 0) {" +
                                "       com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.decreaseMaxHealth(cappedCost);" +
                                "   }" +
                                "   com.megacrit.cardcrawl.core.CardCrawlGame.sound.play(\"BLUNT_FAST\");" +
                                "   com.megacrit.cardcrawl.dungeons.AbstractDungeon.topLevelEffects.add(" +
                                "       new com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect(" +
                                "           com.megacrit.cardcrawl.dungeons.AbstractDungeon.player," +
                                "           com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hb.cX," +
                                "           com.megacrit.cardcrawl.dungeons.AbstractDungeon.player.hb.cY," +
                                "           cappedCost" +
                                "       )" +
                                "   );" +
                                "}");
                    }
                }
            };
        }
    }
}
