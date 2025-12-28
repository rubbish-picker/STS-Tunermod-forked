package tuner.patches.combat;

import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import tuner.helpers.ModHelper;
import tuner.powers.GenrokuPower;

import java.util.ArrayList;

public class OnDrawPileShufflePowerPatch {

    private static void OnDrawPileShuffle() {
        if (AbstractDungeon.player.hasPower(GenrokuPower.POWER_ID)) {
            ((GenrokuPower) AbstractDungeon.player.getPower(GenrokuPower.POWER_ID)).onShuffle();
        }

        //风华特判
        AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
            @Override
            public void update() {
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.drawPile.group.clone();
                        for (AbstractCard c : list) {
                            for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
                                if(mod.identifier(c).equals("tuner:Fuuka2")) {
                                    ModHelper.deckMoveToHand(c);
                                }
                            }
                        }
                        this.isDone = true;
                    }
                });
                this.isDone = true;
            }
        });
    }

    @SpirePatch(clz = EmptyDeckShuffleAction.class, method = "<ctor>")
    public static class ShufflePatch1 {
        public static void Postfix(EmptyDeckShuffleAction __instance) {
            OnDrawPileShuffle();
        }
    }

    @SpirePatch(clz = ShuffleAction.class, method = "update")
    public static class ShufflePatch2 {
        public static void Postfix(ShuffleAction __instance) {
            boolean b = ((Boolean) ReflectionHacks.getPrivate(__instance, ShuffleAction.class, "triggerRelics")).booleanValue();
            if (b)
                OnDrawPileShuffle();
        }
    }

    @SpirePatch(clz = ShuffleAllAction.class, method = "<ctor>")
    public static class ShufflePatch3 {
        public static void Postfix(ShuffleAllAction __instance) {
            OnDrawPileShuffle();
        }
    }
}