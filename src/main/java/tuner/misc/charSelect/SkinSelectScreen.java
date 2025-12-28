package tuner.misc.charSelect;

import basemod.BaseMod;
import basemod.IUIElement;
import basemod.ModPanel;
import basemod.interfaces.ISubscriber;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import tuner.helpers.ConfigHelper;

import java.util.ArrayList;

import static tuner.modCore.PlayerEnum.Tuner_CLASS;

public class SkinSelectScreen implements ISubscriber {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("tuner:SkinSelect");
    public static final float centerX = Settings.WIDTH * 0.8F - 600.0F * Settings.scale;
    public static final float centerY = Settings.HEIGHT * 0.3F;
    private static final ArrayList<Skin> skins;

    public ModPanel settingsPanel;
    public static SkinSelectScreen Inst;
    public SelectPlayVideo playVideoScreen;
    public Hitbox leftHb;

    public Hitbox rightHb;

    public String curName = "";

    public String nextName = "";

    public int index;

    public static Skin getSkin() {
        if (Inst == null)
            return skins.get(0);
        return skins.get(Inst.index);
    }

    public SkinSelectScreen() {
        this.index = ConfigHelper.skinIndexSaved;

        playVideoScreen = new SelectPlayVideo();
        refreshSkinName();
        this.leftHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);
        this.rightHb = new Hitbox(70.0F * Settings.scale, 70.0F * Settings.scale);
        this.settingsPanel = ConfigHelper.initSettings(false);

        BaseMod.subscribe(this);
    }


    public void refreshSkinName() {
        Skin skin = getSkin();
        this.curName = skin.name;
        this.nextName = (skins.get(nextIndex())).name;
    }

    public int prevIndex() {
        return (this.index - 1 < 0) ? (skins.size() - 1) : (this.index - 1);
    }

    public int nextIndex() {
        return (this.index + 1 > skins.size() - 1) ? 0 : (this.index + 1);
    }

    public void update() {
        this.leftHb.move(centerX - 200.0F * Settings.scale, centerY);
        this.rightHb.move(centerX + 200.0F * Settings.scale, centerY);
        updateInput();
        playVideoScreen.update();
    }

    private void updateInput() {
        if (CardCrawlGame.chosenCharacter == Tuner_CLASS) {
            this.leftHb.update();
            this.rightHb.update();
            if (this.leftHb.clicked) {
                this.leftHb.clicked = false;
                CardCrawlGame.sound.play("UI_CLICK_1");
                this.index = prevIndex();
                ConfigHelper.saveSkinIndexSaved(this.index);
                refreshSkinName();
            }
            if (this.rightHb.clicked) {
                this.rightHb.clicked = false;
                CardCrawlGame.sound.play("UI_CLICK_1");
                this.index = nextIndex();
                ConfigHelper.saveSkinIndexSaved(this.index);
                refreshSkinName();
            }
            if (InputHelper.justClickedLeft) {
                if (this.leftHb.hovered)
                    this.leftHb.clickStarted = true;
                if (this.rightHb.hovered)
                    this.rightHb.clickStarted = true;
            }

            for (IUIElement element : this.settingsPanel.getUIElements()) {
                element.update();
            }
        }
    }

    public void render(SpriteBatch sb) {
        Color color = Settings.GOLD_COLOR.cpy();
        color.a /= 2.0F;
        float dist = 60.0F * Settings.scale;
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, this.curName, centerX, centerY, Settings.GOLD_COLOR);
//        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, this.nextName, centerX + dist * 1.5F, centerY + dist, color);
        if (this.leftHb.hovered) {
            sb.setColor(Color.LIGHT_GRAY);
        } else {
            sb.setColor(Color.WHITE);
        }
        sb.draw(ImageMaster.CF_LEFT_ARROW, this.leftHb.cX - 24.0F, this.leftHb.cY - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
        if (this.rightHb.hovered) {
            sb.setColor(Color.LIGHT_GRAY);
        } else {
            sb.setColor(Color.WHITE);
        }
        sb.draw(ImageMaster.CF_RIGHT_ARROW, this.rightHb.cX - 24.0F, this.rightHb.cY - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
        this.rightHb.render(sb);
        this.leftHb.render(sb);

        sb.setColor(Color.WHITE);

        for (IUIElement element : this.settingsPanel.getUIElements()) {
            element.render(sb);
        }
    }

    public void renderVideo(SpriteBatch sb) {
        playVideoScreen.render(sb);
    }

    public static class Skin {
        public int index;

        public String name;

        public Skin(int index) {
            this.index = index;
            this.name = SkinSelectScreen.uiStrings.TEXT[index + 1];
        }
    }

    static {
        skins = new ArrayList<>();
        skins.add(new Skin(0));
        skins.add(new Skin(1));
        skins.add(new Skin(2));
    }
}