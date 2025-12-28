package tuner.misc.charSelect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class SelectPlayVideo {
    private static final String[] videos = {
            "tunerResources/video/output.webm",
            "tunerResources/video/output1.webm",
            "tunerResources/video/output2.webm"
    };

    private static final float StartTime = 0.4F;

    private VideoPlayer videoPlayer;

    private Color cloth = new Color(0, 0, 0, 1);
    private float interalTime = 200F;

    private int index;

    private void change() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        if (videoPlayer == null) {
            over(true);
            return;
        }
        new Thread(() -> {
            try {
                videoPlayer.play(Gdx.files.internal(videos[this.index]));
                videoPlayer.setLooping(true);
            } catch (Exception e) {
                e.printStackTrace();
                over(true);
            }
        }).start();
    }

    public SelectPlayVideo() {
        index = 0;
        change();
    }

    public void update() {
        if (SkinSelectScreen.Inst.index != this.index) {
            this.index = SkinSelectScreen.Inst.index;
            over();
            change();
        }else if(videoPlayer == null){
            over();
            change();
        }

        if (videoPlayer != null) {
            videoPlayer.update();
        }
    }

    public void render(SpriteBatch sb) {
        //视频
        if (videoPlayer != null) {
            Texture texture = videoPlayer.getTexture();
            if (texture != null) {
                sb.setColor(Color.WHITE);
                sb.draw(texture, 0, 0, Settings.WIDTH, Settings.HEIGHT);
            }
        }

        //渐变黑幕
        if (this.interalTime >= 100F || this.interalTime < StartTime) {
            if (this.interalTime >= 100F) this.interalTime = StartTime;

            if (this.interalTime > 0) {
                this.interalTime -= Gdx.graphics.getDeltaTime();
                this.cloth.a = Interpolation.fade.apply(0.0F, 1.0F, this.interalTime / StartTime);
            } else {
                this.interalTime = 50F;
            }
            sb.setColor(this.cloth);
            sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        }
    }

    public void over() {
        over(false);
    }

    public void over(boolean isEnd) {
        if (this.videoPlayer != null) {
            this.videoPlayer.dispose();
            this.videoPlayer = null;
        }
        if (!isEnd)
            this.interalTime = 200F;
    }
}