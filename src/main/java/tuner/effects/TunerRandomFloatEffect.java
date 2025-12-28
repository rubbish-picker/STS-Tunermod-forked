package tuner.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.helpers.ModHelper;

public class TunerRandomFloatEffect extends AbstractGameEffect {
    private TextureRegion img;
    private float x;
    private float y;
    private final float initialX;   // 图片的初始X位置
    private float verticalSpeed; // 垂直方向的速度
    private float waveAmplitude; // 水平方向的波动幅度
    private float waveFrequency; // 波动的频率

    public TunerRandomFloatEffect(float x, float y) {
        this.duration = MathUtils.random(1.5F, 2.0F);

        this.x = x;
        this.initialX = x;
        this.y = y;
        this.img = ModHelper.randomImg();

        this.color = Color.WHITE.cpy();
        this.rotation = 0;
        this.scale = MathUtils.random(0.5F, 2.0F) * Settings.scale;
        // 初始化速度和随机偏移
        resetPositionAndSpeed();
    }

    private void resetPositionAndSpeed() {
        // 垂直方向的速度 (上升速度)
        verticalSpeed = MathUtils.random(100, 200);

        // 水平方向的波动幅度和频率
        waveAmplitude = MathUtils.random(10, 20); // X方向的摆动幅度
        waveFrequency = MathUtils.random(5f, 15f);  // 摆动频率
    }

    @Override
    public void update() {
        // 计算新的X位置，使用正弦波来产生左右摆动的效果
        this.x = initialX + waveAmplitude * MathUtils.sin(waveFrequency * this.duration) * Settings.scale;
        this.y = y + verticalSpeed * Gdx.graphics.getDeltaTime() * Settings.scale;


        this.color.a = Interpolation.pow2Out.apply(1.0F, 0.5F, this.duration/2F);


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
        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose() {
    }
}