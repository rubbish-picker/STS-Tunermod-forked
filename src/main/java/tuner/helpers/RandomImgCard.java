package tuner.helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import tuner.modCore.CardColorEnum;

import java.util.ArrayList;
import java.util.HashMap;

public class RandomImgCard {
    public String id;
    public int index;
    public AbstractCard.CardRarity rarity;

    public RandomImgCard(String id, int index, AbstractCard.CardRarity rarity) {
        this.id = id;
        this.index = index;
        this.rarity = rarity;
    }

    private static ArrayList<RandomImgCard> allImgCards;

    public static void init() {
        allImgCards = new ArrayList<>();

        int i = 0;
        for (AbstractCard card : CardLibrary.getAllCards()) {
            if (card.color == CardColorEnum.ImaginaryColor) {
                allImgCards.add(new RandomImgCard(card.cardID, i, card.rarity));
                i++;
            }
        }
    }

    public static AbstractCard getImgCard(int index) {
        return CardLibrary.getCard(allImgCards.get(index).id).makeCopy();
    }

    private static int getWeight(int i, int j, int k, RandomImgCard r) {
        if (r.rarity == AbstractCard.CardRarity.COMMON) return i;
        if (r.rarity == AbstractCard.CardRarity.UNCOMMON) return j;
        if (r.rarity == AbstractCard.CardRarity.RARE) return k;
        return 0;
    }

    public static RandomImgCard getRandomCard(int i, int j, int k) {
        int totalWeight = 0;
        for (RandomImgCard r : allImgCards) {
            totalWeight += getWeight(i, j, k, r);
        }

        float randomValue = ModHelper.selfRamdom.random() * totalWeight;
        float cumulativeWeight = 0.0F;

        for (RandomImgCard r : allImgCards) {
            cumulativeWeight += getWeight(i, j, k, r);
            if (randomValue <= cumulativeWeight) {
                return r;
            }
        }

        return allImgCards.get(allImgCards.size() - 1);
    }
}
