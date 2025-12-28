package tuner.patches.fullArt;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;
import java.util.Iterator;

public class CardCrawlGamePatch {
    public static ArrayList<AbstractGameEffect> effectList = new ArrayList<>();
    public static ArrayList<AbstractGameEffect> effectsQueue = new ArrayList<>();
    @SpirePatch(clz = CardCrawlGame.class, method = "update")
    public static class UpdatePatch {
        public static void Postfix(CardCrawlGame _inst) {
            Iterator<AbstractGameEffect> i;

            AbstractGameEffect e;
            i = effectList.iterator();

            while (i.hasNext()) {
                e = i.next();
                e.update();

                if (e.isDone) {
                    i.remove();
                }
            }

            i = effectsQueue.iterator();

            while (i.hasNext()) {
                e = i.next();
                effectList.add(e);
                i.remove();
            }
        }
    }
}
