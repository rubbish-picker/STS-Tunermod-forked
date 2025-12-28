package tuner.patches.utils;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import tuner.characters.Tuner;

import static com.badlogic.gdx.math.MathUtils.random;

public class RoomPatch {
    public static boolean smithImgr = false;

    @SpirePatch(clz = RestRoom.class, method = "onPlayerEntry")
    public static class restRoomPatch {
        @SpirePostfixPatch
        public static SpireReturn Postfix(RestRoom _inst) {
            smithImgr = false;
            if (AbstractDungeon.player instanceof Tuner) {
                CardCrawlGame.music.silenceTempBgmInstantly();
                CardCrawlGame.music.silenceBGMInstantly();
                AbstractDungeon.getCurrRoom().playBgmInstantly("tunerRest.mp3");
            }
            return SpireReturn.Continue();
        }
    }
    @SpirePatch(clz = ShopRoom.class, method = "onPlayerEntry")
    public static class shopRoomPatch {
        @SpirePostfixPatch
        public static SpireReturn Postfix(ShopRoom _inst) {
            if (AbstractDungeon.player instanceof Tuner) {
                CardCrawlGame.music.silenceTempBgmInstantly();
                CardCrawlGame.music.silenceBGMInstantly();
                AbstractDungeon.getCurrRoom().playBgmInstantly("tunerShop.mp3");
            }
            return SpireReturn.Continue();
        }
    }
}
