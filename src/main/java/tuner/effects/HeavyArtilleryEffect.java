package tuner.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.Vector;

public class HeavyArtilleryEffect extends AbstractGameEffect {
    private final ShaderProgram shader;
    private float maxHeight = 100F * Settings.scale;

    private final float delayTime;

    private final Texture white = new Texture("tunerResources/img/vfx/horizontal_line.png");

    public HeavyArtilleryEffect(float delayTime, float mul) {
        this.delayTime = delayTime;
        this.startingDuration = this.duration = 0.8F + delayTime;
        this.maxHeight *= mul;

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("tunerResources/shaders/laserShader/vertex.glsl"), Gdx.files.internal("tunerResources/shaders/laserShader/fragment.glsl"));
        if (!shader.isCompiled())
            throw new RuntimeException(shader.getLog());

    }

    @Override
    public void update() {
    }

    @Override
    public void render(SpriteBatch sb) {

        float time = this.startingDuration - this.duration;
        if (time > delayTime) {
            time -= delayTime;


            sb.end();
            sb.begin();

            shader.begin();
            shader.setUniformf("u_time", time);
            shader.setUniformi("u_channel0", 0);
            shader.setUniformf("u_color", new Color(0.7F, 0.1F, 1.0F, 1F));
//            shader.setUniformf("u_color", new Color(1F, 1F, 1.0F, 1F));
            shader.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            ImageMaster.HEALTH_BAR_B.bind(0);

            sb.setShader(shader);

            float beamHeight, width, x, y;
            if (time < 0.4F) {
                beamHeight = (float) (Math.sin(time * 1.57F / 0.4F) * maxHeight);
                width = 50 * Settings.scale * (1 + time / 0.4F);
                x = AbstractDungeon.player.drawX + 65F * Settings.scale;
            } else {
                beamHeight = maxHeight;
                width = 100 * Settings.scale +
                        (time - 0.4F) / (this.startingDuration - 0.4F) * 2 * Settings.WIDTH;
                x = AbstractDungeon.player.drawX + 65F * Settings.scale +
                        (time - 0.4F) / (this.startingDuration - 0.4F) * 0.2F * Settings.WIDTH;
            }
            y = AbstractDungeon.player.drawY + 100F * Settings.scale - beamHeight / 2F;

            sb.draw(ImageMaster.HEALTH_BAR_B, x, y, width, beamHeight);

            sb.setShader(null);
        }
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    @Override
    public void dispose() {
        shader.dispose();
    }
}
