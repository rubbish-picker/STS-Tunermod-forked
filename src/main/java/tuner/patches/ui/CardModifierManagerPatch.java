package tuner.patches.ui;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.helpers.ModHelper;

import java.util.function.Predicate;

public class CardModifierManagerPatch {
    @SpirePatch(clz = CardModifierManager.class, method = "deferredConditionalRemoval")
    public static class ssss {
        @SpirePostfixPatch
        public static void Postfix(final AbstractCard card, Predicate<AbstractCardModifier> condition) {
            if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT)
                AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                    @Override
                    public void update() {
                        ModHelper.CalculateEffect(card);
                        this.isDone = true;
                    }
                });
        }
    }
}