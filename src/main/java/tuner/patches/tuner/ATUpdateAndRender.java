package tuner.patches.tuner;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.relics.ATRelic;

public class ATUpdateAndRender {

    @SpirePatch(clz = AbstractDungeon.class, method = "update")
    public static class UpdateAT {
        public static void Postfix(AbstractDungeon _inst) {
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT
                    && AbstractDungeon.player.drawPile != null && ATRelic.at != null
                    && AbstractDungeon.player.hasRelic(ATRelic.ID))
                ATRelic.at.update();
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class RenderAT {
        @SpireInsertPatch(rloc = 28)
        public static void insert(AbstractDungeon _inst, SpriteBatch sb) {
            if ((AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT
                    && AbstractDungeon.player.drawPile != null && ATRelic.at != null
                    && AbstractDungeon.player.hasRelic(ATRelic.ID))
                ATRelic.at.render(sb);
        }
    }
}
