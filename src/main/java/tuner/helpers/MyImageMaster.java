package tuner.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class MyImageMaster {
    public static Texture tuner1;
    public static Texture tuner2;
    public static Texture tuner3;
    public static Texture tuner4;
    public static Texture tuner5;
    public static Texture charShadow;
    public static Texture pin;
    public static Texture magicNumber;

    public static Texture CardBg = new Texture("tunerResources/img/fullArt/ui/CardBg.png");
    public static TextureAtlas.AtlasRegion CardBgAtlas = new TextureAtlas.AtlasRegion(CardBg, 0, 0, CardBg.getWidth(), CardBg.getHeight());
    public static Texture CardBg512 = new Texture("tunerResources/img/fullArt/ui/CardBg512.png");
    public static TextureAtlas.AtlasRegion CardBg512Atlas = new TextureAtlas.AtlasRegion(CardBg512, 0, 0, CardBg512.getWidth(), CardBg512.getHeight());
    public static Texture CardBgStar = new Texture("tunerResources/img/fullArt/ui/star.png");
    public static TextureAtlas.AtlasRegion CardBgStarAtlas = new TextureAtlas.AtlasRegion(CardBgStar, 0, 0, CardBgStar.getWidth(), CardBgStar.getHeight());


    static {
        tuner1 = new Texture("tunerResources/img/vfx/tuner/1.png");
        tuner2 = new Texture("tunerResources/img/vfx/tuner/2.png");
        tuner3 = new Texture("tunerResources/img/vfx/tuner/3.png");
        tuner4 = new Texture("tunerResources/img/vfx/tuner/4.png");
        tuner5 = new Texture("tunerResources/img/vfx/tuner/5.png");
        charShadow = new Texture("tunerResources/img/char/shadow.png");
        pin = new Texture("tunerResources/img/UI/pin.png");
        magicNumber = new Texture("tunerResources/img/UI/MagicNumber.png");
    }
}
