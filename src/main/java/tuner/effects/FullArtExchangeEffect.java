package tuner.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.interfaces.FullArtSubscriber;
import tuner.patches.fullArt.CardCrawlGamePatch;

import java.util.*;

public class FullArtExchangeEffect extends AbstractGameEffect {
    private static final float START_DURATION = 0.25F;
    private static Map<String, Integer> cardsInExchange = new HashMap<>();
    private static Map<String, Float> cardsScaleX = new HashMap<>();

    private AbstractCard targetCard;
    private boolean isHalf = false;

    public static float shouldExchange(String cardID){
        if(cardsInExchange.containsKey(cardID)){
            if(!cardsScaleX.containsKey(cardID)){
                cardsScaleX.put(cardID, 1F);
            }
            return cardsScaleX.get(cardID);
        }
        return 1F;
    }

    public FullArtExchangeEffect(AbstractCard c) {
        if (!(c instanceof FullArtSubscriber)) {
            this.isDone = true;
            return;
        }

        this.duration = START_DURATION;
        this.targetCard = c;

        int count = cardsInExchange.getOrDefault(c.cardID, 0);
        cardsInExchange.put(c.cardID, count + 1);
        if (count > 0) {
            this.isDone = true;
        }
    }

    public FullArtExchangeEffect(AbstractCard c, boolean self) {
        this.duration = START_DURATION;
        this.targetCard = c;
    }

    public void update() {
        if (!isDone) {
            float mapped = (float) (Math.PI * this.duration / START_DURATION);

            cardsScaleX.put(targetCard.cardID, 1 - (float) Math.sin(mapped));

            if (cardsInExchange.containsKey(targetCard.cardID) && cardsInExchange.get(targetCard.cardID) > 0) {
                this.duration -= Gdx.graphics.getDeltaTime() * cardsInExchange.get(targetCard.cardID);
            } else {
                this.isDone = true;
                return;
            }

            //特判-2c
            if(targetCard.cost == -2)
                duration = -1;

            if(this.duration < START_DURATION/2F && !isHalf){
                isHalf = true;
                FullArtSubscriber.exchangeFullArt(targetCard);
            }

            if (this.duration < 0.0F) {
                this.isDone = true;

                if (cardsInExchange.get(targetCard.cardID) > 1) {
                    cardsInExchange.put(targetCard.cardID, cardsInExchange.get(targetCard.cardID) - 1);
                    CardCrawlGamePatch.effectsQueue.add(new FullArtExchangeEffect(targetCard, true));
                } else cardsInExchange.remove(targetCard.cardID);
            }
        }
    }

    public void render(SpriteBatch spriteBatch) {
    }

    public void dispose() {
    }
}