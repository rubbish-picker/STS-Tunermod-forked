package tuner.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.relics.ATRelic;

public class ShowATEffect extends AbstractGameEffect {

    private static final float EFFECT_DUR = 0.5f;

    private final boolean reverse;

    public ShowATEffect(boolean reverse) {
        duration = EFFECT_DUR;
        this.reverse = reverse;
        if (reverse) ATRelic.at.onCloseAni = true;
    }

    private int getCount(float duration) {
        int tmp = (int) (duration / EFFECT_DUR * 5);

        if (reverse) return tmp;
        else return 5 - tmp;
    }

    @Override
    public void update() {
        if (ATRelic.at == null) {
            this.isDone = true;
            return;
        }

        if (duration == EFFECT_DUR) {
            ATRelic.at.hide = false;
        }

        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0.0F) {

            if (reverse) {
                ATRelic.at.init();
            }

            this.isDone = true;
            return;
        }

        ATRelic.at.MaxCount = getCount(duration);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

    }

    @Override
    public void dispose() {

    }
}
