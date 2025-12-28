package tuner.patches.combat;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import java.util.ArrayList;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class CardGroupPatch {
    @SpirePatch(clz = CardGroup.class, method = "initializeDeck")
    public static class RemoveImg {
        @SpireInsertPatch(rloc = 2)
        public static void Insert(CardGroup group, CardGroup masterDeck, CardGroup ___copy) {
            ArrayList<AbstractCard> tmp = (ArrayList<AbstractCard>) ___copy.group.clone();
            for (AbstractCard c : tmp) {
                if (c.type == Imaginary) ___copy.removeCard(c);
            }
        }
    }
}