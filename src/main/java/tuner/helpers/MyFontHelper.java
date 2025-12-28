package tuner.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

public class MyFontHelper {
    public static BitmapFont titleFont;

    static {
        FileHandle fontFile = null;
        float fontScale;
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

        switch (Settings.language) {
            case ZHS:
                fontFile = Gdx.files.internal("font/zhs/NotoSansMonoCJKsc-Regular.otf");
                break;
            case ZHT:
                fontFile = Gdx.files.internal("font/zht/NotoSansCJKtc-Regular.otf");
                break;
            case EPO:
                fontFile = Gdx.files.internal("font/epo/Andada-Regular.otf");
                break;
            case GRE:
                fontFile = Gdx.files.internal("font/gre/Roboto-Regular.ttf");
                break;
            case JPN:
                fontFile = Gdx.files.internal("font/jpn/NotoSansCJKjp-Regular.otf");
                break;
            case KOR:
                fontFile = Gdx.files.internal("font/kor/GyeonggiCheonnyeonBatangBold.ttf");
                break;
            case POL:
            case RUS:
            case UKR:
                fontFile = Gdx.files.internal("font/rus/FiraSansExtraCondensed-Regular.ttf");
                break;
            case SRP:
            case SRB:
                fontFile = Gdx.files.internal("font/srb/InfluBG.otf");
                break;
            case THA:
                fontFile = Gdx.files.internal("font/tha/CSChatThaiUI.ttf");
                fontScale = 0.95F;
                break;
            case VIE:
                fontFile = Gdx.files.internal("font/vie/Grenze-Regular.ttf");
                break;
            default:
                fontFile = Gdx.files.internal("font/Kreon-Regular.ttf");
        }
        param.hinting = FreeTypeFontGenerator.Hinting.Slight;
        param.kerning = true;
        param.borderWidth = 0.0F;

        param.characters = "";
        param.incremental = true;

        if (Settings.BIG_TEXT_MODE)
            param.size = Math.round(20.0F * Settings.scale * 1.2F);
        else param.size = Math.round(20.0F * Settings.scale);

        param.gamma = 0.9F;
        param.spaceX = 0;
        param.borderColor = new Color(0.35F, 0.35F, 0.35F, 1.0F);
        param.borderStraight = false;
        param.borderWidth = 2.0F * Settings.scale;
        param.borderGamma = 0.9F;
        param.shadowOffsetX = Math.round(2.0F * Settings.scale);
        param.shadowOffsetY = Math.round(2.0F * Settings.scale);
        param.shadowColor = new Color(0.0F, 0.0F, 0.0F, 0.25F);
        if (true) {
            param.minFilter = Texture.TextureFilter.Linear;
            param.magFilter = Texture.TextureFilter.Linear;
        } else {
            param.minFilter = Texture.TextureFilter.Nearest;
            param.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        }
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(fontFile);

        titleFont = g.generateFont(param);
        titleFont.setUseIntegerPositions(!true);
        titleFont.getData().markupEnabled = true;
        if (LocalizedStrings.break_chars != null) {
            titleFont.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        titleFont.getData().fontFile = fontFile;
    }
}
