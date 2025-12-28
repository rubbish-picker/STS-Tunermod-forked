package tuner.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import tuner.helpers.ConfigHelper;
import tuner.helpers.ModHelper;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.FtueTip;

import java.io.IOException;

import static tuner.helpers.ConfigHelper.config;

public class MessageCaller extends AbstractGameAction {
    public static final int codeCount = 3;
    private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString("TunerTutorials");
    public static final String[] txt = tutorialStrings.TEXT;
    public static final String[] LABEL = tutorialStrings.LABEL;
    private static final Texture[] img = {
            ImageMaster.loadImage("tunerResources/img/tutorial/0.png"),
            ImageMaster.loadImage("tunerResources/img/tutorial/1.png"),
            ImageMaster.loadImage("tunerResources/img/tutorial/2.png"),
            ImageMaster.loadImage("tunerResources/img/tutorial/3.png")
    };
    private static final float[] x = {
            Settings.WIDTH*0.55F,
            Settings.WIDTH*0.3F,
            Settings.WIDTH*0.4F,
            Settings.WIDTH*0.2F,
            2,3,4
    };
    private static final float[] y = {
            Settings.HEIGHT*0.65F,
            Settings.HEIGHT*0.5F,
            Settings.HEIGHT*0.7F,
            Settings.HEIGHT*0.75F,
            1,2,3,4
    };

    public static int CODE = 0;

    public int code;
    private boolean firstTime = true;

    public MessageCaller(int code) {
        this.code = code;
    }

    public void update() {
        if (ConfigHelper.activeTutorials && this.firstTime) {
            this.firstTime = false;
            if (code == codeCount) {
                ConfigHelper.activeTutorials = false;
                try {
                    config.setBool("activeTutorials", false);
                    config.save();
                } catch (IOException e) {
                    ModHelper.logger.warn(e);
                }
            }

            CODE = code;

            AbstractDungeon.ftue = new FtueTip(LABEL[code], txt[code], x[code], y[code], FtueTip.TipType.NO_FTUE);
        }

        this.isDone = true;
    }

    public static void rrender(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        Texture texture = img[CODE];
        sb.draw(texture,
                Settings.WIDTH * 0.8F - texture.getWidth()/2F,
                Settings.HEIGHT * 0.2F - texture.getHeight()/2F,
                texture.getWidth() / 2F, texture.getHeight() / 2F,
                texture.getWidth(), texture.getHeight(),
                Settings.scale, Settings.scale,
                0.0F, 0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
    }
}
