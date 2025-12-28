package tuner.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class FlyingBigDaggerEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private float randomX;
    private float nextX;
    private float destY;
    private float scaleMultiplier;
    private static final float DUR = 0.5F;
    private TextureAtlas.AtlasRegion img;
    private boolean playedSound = false;

    public FlyingBigDaggerEffect(float x, float y, boolean shouldFlip) {
        this.img = ImageMaster.DAGGER_STREAK;
        this.x = x - (float) this.img.packedWidth / 2.0F;
        this.randomX = 0.1F * Settings.WIDTH;
        this.nextX = this.x + this.randomX;
        this.destY = y;
        this.y = this.destY - (float) this.img.packedHeight / 2.0F;
        this.startingDuration = 0.5F;
        this.duration = 0.5F;
        this.scaleMultiplier = MathUtils.random(1.2F, 1.5F);
        this.scale = 1F * Settings.scale;
        if (shouldFlip) {
            this.rotation = 180.0F;
        } else {
            this.rotation = 0;
        }

        this.color = Color.PURPLE.cpy();
        this.color.a = 0.0F;
    }

    public void update() {
        if (!this.playedSound) {
            if (MathUtils.random(1) == 0)
                CardCrawlGame.sound.play("tunerLongShoot1");
            else
                CardCrawlGame.sound.play("tunerLongShoot2");
            this.playedSound = true;
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        Vector2 derp = new Vector2(MathUtils.cos(0.017453292F * this.rotation), MathUtils.sin(0.017453292F * this.rotation));
        this.x += derp.x * Gdx.graphics.getDeltaTime() * 3500.0F * this.scaleMultiplier * Settings.scale;
        this.y += derp.y * Gdx.graphics.getDeltaTime() * 3500.0F * this.scaleMultiplier * Settings.scale;

        if (this.x > this.nextX) {
            this.nextX += this.randomX * MathUtils.random(-0.0F, 2.0F);
            AbstractDungeon.effectsQueue.add(new TunerRandomFloatEffect(this.x, this.y - 20F * Settings.scale));
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
        }

        if (this.duration > 0.25F) {
            this.color.a = Interpolation.pow5In.apply(1.0F, 0.0F, (this.duration - 0.25F) * 4.0F);
        } else {
            this.color.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration * 4.0F);
        }

        this.scale += Gdx.graphics.getDeltaTime() * this.scaleMultiplier;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.setBlendFunction(770, 1);
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.3F, this.scale * 0.8F, this.rotation - 3.0F);
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.3F, this.scale * 0.8F, this.rotation + MathUtils.random(2.0F, 3.0F));
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.4F, this.scale * 0.5F, this.rotation - MathUtils.random(-1.0F, 2.0F));
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.7F, this.scale * 0.9F, this.rotation + MathUtils.random(3.0F, 5.0F));
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 1.5F, this.scale * MathUtils.random(1.4F, 1.6F), this.rotation);
        Color c = Color.SLATE.cpy();
        c.a = this.color.a;
        sb.setColor(c);
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale, this.scale * MathUtils.random(0.8F, 1.2F), this.rotation);
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale, this.scale * MathUtils.random(0.4F, 0.6F), this.rotation);
        sb.draw(this.img, this.x + MathUtils.random(-10.0F, 10.0F) * Settings.scale, this.y, (float) this.img.packedWidth / 2.0F, 0.0F, (float) this.img.packedWidth, (float) this.img.packedHeight, this.scale * 0.5F, this.scale * 0.7F, this.rotation);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {
    }
}
