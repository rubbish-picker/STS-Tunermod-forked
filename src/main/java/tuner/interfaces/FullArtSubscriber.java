package tuner.interfaces;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import tuner.effects.FullArtExchangeEffect;
import tuner.helpers.ModHelper;
import tuner.helpers.MyImageMaster;
import tuner.patches.fullArt.CardCrawlGamePatch;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.tools.hiero.unicodefont.effects.EffectUtil.booleanValue;

public interface FullArtSubscriber extends OnRightClickSubscriber {
    Map<String, Boolean> FullArtsAvail = new HashMap<>();
    Map<String, Texture> FullArtsText = new HashMap<>();

    static boolean shouldShowFullArt(String cardID) {
        return FullArtsAvail.get(cardID);
    }

    static boolean initShouldShowFullArt(AbstractCard card) {
        if (!(card instanceof FullArtSubscriber))
            return false;
        if (!FullArtsAvail.containsKey(card.cardID)) {
            FullArtsAvail.put(card.cardID, false);
        }
        if (!FullArtsText.containsKey(card.cardID)) {
            FullArtsText.put(card.cardID, ImageMaster.loadImage(getFullArtPortraitImgPath(card.cardID)));
        }
        return shouldShowFullArt(card.cardID);
    }

    static void exchangeFullArt(AbstractCard c) {
        if (c instanceof FullArtSubscriber) {
            if (FullArtsAvail.containsKey(c.cardID)) {
                FullArtsAvail.put(c.cardID, !FullArtsAvail.get(c.cardID));
            } else {
                initShouldShowFullArt(c);
            }
        }
    }

    static String getFullArtPortraitImgPath(String cardID) {
        String[] parts = cardID.split(":");
        if (parts.length > 1)
            cardID = parts[1];
        if (Gdx.files.internal("tunerResources/img/fullArt/" + cardID + ".png").exists())
            return "tunerResources/img/fullArt/" + cardID + ".png";
        else return "tunerResources/img/fullArt/temp.png";
    }

    default String getFullArtPortraitImgPath() {
        return getFullArtPortraitImgPath(((AbstractCard) this).cardID);
    }

    default TextureAtlas.AtlasRegion getFullArtPortrait() {
        Texture t = FullArtsText.get(((AbstractCard) this).cardID);
        return new TextureAtlas.AtlasRegion(t, 0, 0, t.getWidth(), t.getHeight());
    }

    @Override
    default void onRightClick() {
        CardCrawlGamePatch.effectList.add(new FullArtExchangeEffect((AbstractCard) this));
    }

    //下面放的小工具
    static Texture getAttackImage(int dmg) {
        if (dmg < 4)
            return ImageMaster.INTENT_ATK_1;
        if (dmg < 8)
            return ImageMaster.INTENT_ATK_2;
        if (dmg < 12)
            return ImageMaster.INTENT_ATK_3;
        if (dmg < 16)
            return ImageMaster.INTENT_ATK_4;
        if (dmg < 20)
            return ImageMaster.INTENT_ATK_5;
        if (dmg < 24)
            return ImageMaster.INTENT_ATK_6;
        return ImageMaster.INTENT_ATK_7;
    }

    static Texture getDefendImage() {
        return ImageMaster.INTENT_DEFEND;
    }

    static Texture getMGCImage() {
        return ImageMaster.INTENT_BUFF_L;
    }

    static TextureAtlas.AtlasRegion getCardBg() {
        return MyImageMaster.CardBgAtlas;
    }

    static TextureAtlas.AtlasRegion getCardBg512() {
        return MyImageMaster.CardBg512Atlas;
    }
}