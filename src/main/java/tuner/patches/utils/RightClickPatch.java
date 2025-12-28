package tuner.patches.utils;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import tuner.interfaces.OnRightClickInHandSubscriber;
import tuner.interfaces.OnRightClickSubscriber;

public class RightClickPatch {
    public static final float DELAY = 0.2f;

    public static float locked = 0;

    public static void update() {
        if (AbstractDungeon.player != null) {
            if (AbstractDungeon.player.hoveredCard != null && AbstractDungeon.player.isDraggingCard) {
                locked = DELAY;
            } else if(locked > 0) locked -= Gdx.graphics.getDeltaTime();

        }
    }

    public static class State {
        public boolean rightClickStarted = false;
        public boolean rightClickInHand = false;
        public boolean rightClick = false;
        public boolean rightClickHoverd = false;
    }

    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class Fields {
        public static SpireField<State> state = new SpireField<>(State::new);
    }

    @SpirePatch(clz = AbstractCard.class, method = "update")
    public static class UpdatePatch {
        public static void Postfix(AbstractCard clickable) {
            if (AbstractDungeon.player == null)
                return;

            State state = Fields.state.get(clickable);

            if (InputHelper.mX > clickable.hb.x &&
                    InputHelper.mX < clickable.hb.x + clickable.hb.width
                    && InputHelper.mY > clickable.hb.y &&
                    InputHelper.mY < clickable.hb.y + clickable.hb.height
            ) {
                state.rightClickHoverd = true;
            } else {
                state.rightClickHoverd = false;
            }

            if (state.rightClickStarted && InputHelper.justReleasedClickRight) {
                if (AbstractDungeon.player != null && clickable.isHoveredInHand(1.0F))
                    state.rightClickInHand = true;
                if (state.rightClickHoverd)
                    state.rightClick = true;
                state.rightClickStarted = false;
            }

            if (clickable.hb != null &&
                    state.rightClickHoverd &&
                    InputHelper.justClickedRight)
                state.rightClickStarted = true;

            if (state.rightClickInHand) {
                if (clickable instanceof OnRightClickInHandSubscriber) {
                    if (locked <= 0)
                        ((OnRightClickInHandSubscriber) clickable).onRightClickInHand();
                }
                state.rightClickInHand = false;
            }

            if (state.rightClick) {
                if (clickable instanceof OnRightClickSubscriber) {
                    if (locked <= 0)
                        ((OnRightClickSubscriber) clickable).onRightClick();
                }
                state.rightClick = false;
            }
        }
    }
}
