package tuner.patches.ui;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import tuner.interfaces.OnObtainSubscriber;

import java.util.ArrayList;

import static tuner.modCore.CardTypeEnum.Imaginary;

public class ObtainCardPatch {

    @SpirePatch(clz = SoulGroup.class, method = "obtain")
    public static class onObtain {
        @SpirePostfixPatch
        public static SpireReturn Postfix(SoulGroup _inst, AbstractCard card, boolean obtainCard) {
            if (card instanceof OnObtainSubscriber) {
                ((OnObtainSubscriber) card).onObtain();
            }

            ArrayList<AbstractCard> cardsToRemove = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();
            ArrayList<AbstractCard> cardsToAdd = new ArrayList<>();
            for(AbstractCard c : cardsToRemove){
                if(c.type == Imaginary){
                    AbstractDungeon.player.masterDeck.group.remove(c);
                    cardsToAdd.add(c);
                }
            }
            for(AbstractCard c : cardsToAdd){
                AbstractDungeon.player.masterDeck.group.add(c);
            }

            return SpireReturn.Continue();
        }
    }
}
