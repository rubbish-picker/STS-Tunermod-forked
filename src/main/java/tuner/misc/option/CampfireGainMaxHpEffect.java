package tuner.misc.option;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

/**
 * Campfire effect: increase max HP without healing.
 */
public class CampfireGainMaxHpEffect extends AbstractGameEffect {

    public static final String[] TEXT = (com.megacrit.cardcrawl.core.CardCrawlGame.languagePack
            .getUIString("tuner:CampfireGainMaxHpEffect")).TEXT;

    private final int amount;
    private final Color screenColor;
    private boolean applied = false;

    public CampfireGainMaxHpEffect(int amount) {
        this.amount = amount;
        this.duration = 1.0f;
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.screenColor.a = 0.0f;

        AbstractDungeon.overlayMenu.proceedButton.hide();
    }

    @Override
    public void update() {
        // Fade in/out like other campfire effects.
        if (!AbstractDungeon.isScreenUp) {
            this.duration -= Gdx.graphics.getDeltaTime();
            updateBlackScreenColor();
        }

        if (!applied && this.duration < 0.5f) {
            applied = true;

            // Increase max HP, but do NOT increase current HP.
            // Note: In some STS versions, increaseMaxHp may also raise currentHealth.
            // We explicitly restore currentHealth to the pre-gain value.
            int oldCurrentHp = AbstractDungeon.player.currentHealth;
            AbstractDungeon.player.increaseMaxHp(amount, true);
            if (AbstractDungeon.player.currentHealth > oldCurrentHp) {
                AbstractDungeon.player.currentHealth = Math.min(oldCurrentHp, AbstractDungeon.player.maxHealth);
            }

            // VFX: keep minimal to avoid depending on campfire vfx classes that may vary by game version.
        }

        if (this.duration < 0.0f) {
            this.isDone = true;
            if (CampfireUI.hidden) {
                AbstractRoom.waitTimer = 0.0f;
                AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
                ((RestRoom) AbstractDungeon.getCurrRoom()).cutFireSound();
            }
        }
    }

    private void updateBlackScreenColor() {
        if (this.duration > 0.5F) {
            this.screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - 0.5F) / 0.5F);
        } else {
            this.screenColor.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration / 0.5F);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(com.megacrit.cardcrawl.helpers.ImageMaster.WHITE_SQUARE_IMG,
                0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);

        // No custom UI; keep it simple.
    }

    @Override
    public void dispose() {
    }
}
