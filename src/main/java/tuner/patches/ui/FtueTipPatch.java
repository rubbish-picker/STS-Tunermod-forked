package tuner.patches.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.FtueTip;
import tuner.action.MessageCaller;
import tuner.relics.ATRelic;

import static com.megacrit.cardcrawl.ui.FtueTip.TipType.NO_FTUE;
import static tuner.action.MessageCaller.CODE;

public class FtueTipPatch {
    @SpirePatch(clz = FtueTip.class, method = "render")
    public static class FtueTipPatch2 {
        public static void Postfix(FtueTip __instance, SpriteBatch sb) {
            if (__instance.type == NO_FTUE
                    && (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT
                    && AbstractDungeon.player.drawPile != null && ATRelic.at != null
                    && AbstractDungeon.player.hasRelic(ATRelic.ID)) {

                if(CODE == 1)ATRelic.at.render(sb);
                if(CODE == 2)ATRelic.at.dorlach.render(sb);

                MessageCaller.rrender(sb);
            }
        }
    }

}