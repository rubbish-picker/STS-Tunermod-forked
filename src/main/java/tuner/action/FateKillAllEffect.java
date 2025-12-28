package tuner.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.megacrit.cardcrawl.actions.common.InstantKillAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.helpers.ModHelper;

public class FateKillAllEffect extends AbstractGameEffect {

    private static VideoPlayer videoPlayer;

    public boolean firstTime = true;

    public void playVideo() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        if (videoPlayer == null) {
            ModHelper.logger.error("============VideoPlayer creation failed========");
            over();
            return;
        }

        new Thread(() -> {
            try {
                videoPlayer.play(Gdx.files.internal("tunerResources/video/fate.webm"));
                videoPlayer.setOnCompletionListener(listener -> over());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void over() {
//        ModHelper.logger.info("==============Fate over============");
        if (videoPlayer != null) {
            videoPlayer.dispose();
            videoPlayer = null;
        }
        (AbstractDungeon.getCurrRoom()).cannotLose = false;
        for (AbstractMonster mo : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
            mo.halfDead = false;
            if (!mo.isDead) {
                AbstractDungeon.actionManager.addToTop(new InstantKillAction(mo));
            }
        }
        this.isDone = true;
    }

    @Override
    public void update() {
        if (firstTime) {
            firstTime = false;
            playVideo();
        }
        if (videoPlayer != null) {
            videoPlayer.update();
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (videoPlayer != null) {
            Texture texture = videoPlayer.getTexture();
            if (texture != null) {
                spriteBatch.setColor(Color.WHITE);
                spriteBatch.draw(texture, Settings.WIDTH * 0.25F, Settings.HEIGHT * 0.25F,
                        Settings.WIDTH / 2F, Settings.HEIGHT / 2F);
            }
        }
    }

    @Override
    public void dispose() {
        over();
    }
}
