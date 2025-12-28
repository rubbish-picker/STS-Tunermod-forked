package tuner.effects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.StarBounceEffect;
import tuner.helpers.ModHelper;
import tuner.helpers.MyImageMaster;

public class TunerMoveEffect extends AbstractGameEffect {
    private float x;
    private float y;
    private boolean playedSound = false;

    public TunerMoveEffect(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void update() {
        if (!playedSound) {
            playedSound = true;
            int tmp = MathUtils.random(2);
            if (tmp == 0)
                CardCrawlGame.sound.play("tunerTunerShoot1");
            else if (tmp == 1)
                CardCrawlGame.sound.play("tunerTunerShoot2");
            else if (tmp == 2)
                CardCrawlGame.sound.play("tunerTunerShoot3");
        }

        for (int i = 0; i < 12; ++i) {
            AbstractDungeon.effectsQueue.add(new AbstractGameEffect() {
                private TextureRegion img;
                private static final float DUR = 1.0F;
                private float x;
                private float y;
                private float vX;
                private float vY;
                private float floor;

                {
                    this.img = ModHelper.randomImg();

                    this.duration = MathUtils.random(1F, 1.5F);
                    this.x = TunerMoveEffect.this.x - (float) (this.img.getRegionWidth() / 2);
                    this.y = TunerMoveEffect.this.y - (float) (this.img.getRegionHeight() / 2);
                    this.color = Color.WHITE.cpy();
                    this.color.a = 0.0F;
                    this.rotation = MathUtils.random(0.0F, 360.0F);
                    this.scale = MathUtils.random(0.5F, 2.0F) * Settings.scale;
                    this.vX = MathUtils.random(-700.0F, 700.0F) * Settings.scale;
                    this.vY = MathUtils.random(200.0F, 500.0F) * Settings.scale;
                    this.floor = MathUtils.random(100.0F, 250.0F) * Settings.scale;
                }

                @Override
                public void update() {
                    this.vY += 2000.0F * Settings.scale / this.scale * Gdx.graphics.getDeltaTime();
                    this.x += this.vX * Gdx.graphics.getDeltaTime();
                    this.y += this.vY * Gdx.graphics.getDeltaTime();
                    Vector2 test = new Vector2(this.vX, this.vY);
                    this.rotation = test.angle();
                    if (this.y < this.floor) {
                        this.vY = -this.vY * 0.75F;
                        this.y = this.floor + 0.1F;
                        this.vX *= 1.1F;
                    }

                    if (1.0F - this.duration < 0.1F) {
                        this.color.a = Interpolation.fade.apply(0.0F, 1.0F, (1.0F - this.duration) * 10.0F);
                    } else {
                        this.color.a = Interpolation.pow2Out.apply(0.0F, 1.0F, this.duration);
                    }

                    this.duration -= Gdx.graphics.getDeltaTime();
                    if (this.duration < 0.0F) {
                        this.isDone = true;
                    }

                }

                @Override
                public void render(SpriteBatch sb) {
                    sb.setBlendFunction(770, 1);
                    sb.setColor(this.color);
                    sb.draw(this.img, this.x, this.y, (float) this.img.getRegionWidth() / 2.0F, (float) this.img.getRegionHeight() / 2.0F, (float) this.img.getRegionWidth(), (float) this.img.getRegionHeight(), this.scale * MathUtils.random(0.8F, 1.2F), this.scale * MathUtils.random(0.4F, 0.6F), this.rotation);
                    sb.draw(this.img, this.x, this.y, (float) this.img.getRegionWidth() / 2.0F, (float) this.img.getRegionHeight() / 2.0F, (float) this.img.getRegionWidth(), (float) this.img.getRegionHeight(), this.scale * MathUtils.random(0.8F, 1.2F), this.scale * MathUtils.random(0.4F, 0.6F), this.rotation);
                    sb.setBlendFunction(770, 771);
                }

                @Override
                public void dispose() {
                }
            });
        }

        this.isDone = true;
    }

    public void render(SpriteBatch sb) {
    }

    public void dispose() {
    }
}
