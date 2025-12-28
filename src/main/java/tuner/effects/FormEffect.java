package tuner.effects;

import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.helpers.ModHelper;

public class FormEffect extends AbstractGameEffect {
    private final float x;

    private final float y;

    public FormEffect(float x, float y) {
        this.x = x;
        this.y = y;
        this.duration = 0.2F;
    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        float scale = MathUtils.random(0.9F, 1.1F);
        float life = MathUtils.random(0.5F, 0.7F);
        AbstractDungeon.effectsQueue.add((new VfxBuilder(

                ModHelper.randomImg(true),
                this.x + MathUtils.random(-200.0F, 200.0F) * Settings.scale,
                this.y + MathUtils.random(-150.0F, 150.0F) * Settings.scale, 0.05F))

                .useAdditiveBlending()
                .setColor(Color.WHITE)
                .scale(0.0F, scale, VfxBuilder.Interpolations.EXP10OUT)
                .fadeIn(0.05F)
                .andThen(life)
                .oscillateAlpha(0.5F, 1.0F, 3.0F)
                .oscillateScale(scale * 0.8F, scale * 1.1F, 3.0F)
                .andThen(0.3F)
                .scale(scale, 0.0F, VfxBuilder.Interpolations.EXP10OUT)
                .fadeOut(0.05F)
                .build());

        if (this.duration < 0.0F)
            this.isDone = true;
    }

    public void render(SpriteBatch spriteBatch) {
    }

    public void dispose() {
    }
}