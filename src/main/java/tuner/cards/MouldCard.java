package tuner.cards;

import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.helpers.ModHelper;
import tuner.helpers.MyImageMaster;
import tuner.interfaces.FullArtSubscriber;
import tuner.modCore.CardColorEnum;
import tuner.modCore.CardTypeEnum;

import java.util.ArrayList;
import java.util.List;

public abstract class MouldCard extends CustomCard {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("tuner:ImaginaryCard")).TEXT;

    private static final TextureAtlas cardAtlas = new TextureAtlas(Gdx.files.internal("cards/cards.atlas"));

    public CardStrings cardStrings;
    public String DESCRIPTION;
    public String DESCRIPTION_UPG;
    public String[] EXTENDED_DESCRIPTION;

    public AbstractCardModifier modifier = null;
    public Texture tinyPic;

    public MouldCard(
            String NAME,
            int COST,
            CardType TYPE,
            CardRarity RARITY,
            CardTarget TARGET
    ) {
        this(NAME, COST, TYPE, RARITY, TARGET, CardColorEnum.TunerColor);
    }

    public MouldCard(
            String NAME,
            int COST,
            CardType TYPE,
            CardRarity RARITY,
            CardTarget TARGET,
            CardColor color
    ) {
        super("tuner:" + NAME, rtCardStrings(NAME).NAME, rtPicPath(NAME, TYPE),
                COST, rtCardStrings(NAME).DESCRIPTION, TYPE, color, RARITY, TARGET);
        cardStrings = rtCardStrings(NAME);
        DESCRIPTION = cardStrings.DESCRIPTION;
        DESCRIPTION_UPG = cardStrings.UPGRADE_DESCRIPTION;
        EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;

        if (color == CardColorEnum.ImaginaryColor)
            initTinyPic(NAME);
    }

    public void initTinyPic(String s) {
        s = "tunerResources/img/tinyCards/" + s + ".png";
        if (Gdx.files.internal(s).exists())
            this.tinyPic = new Texture(s);
        else this.tinyPic = new Texture("tunerResources/img/tinyCards/temp.png");
    }

    @Override
    public List<TooltipInfo> getCustomTooltipsTop() {
        if (this.type == CardTypeEnum.Imaginary) {
            ArrayList<TooltipInfo> list = new ArrayList<>();
            list.add(new TooltipInfo(TEXT[0], TEXT[1]));
            return list;
        } else return null;

    }

    public static String rtPicPath(String name, CardType type) {
        if (Gdx.files.internal("tunerResources/img/cards/" + name + ".png").exists())
            return "tunerResources/img/cards/" + name + ".png";

        if (type == CardType.ATTACK)
            return "tunerResources/img/cards/attack.png";
        else if (type == CardType.SKILL)
            return "tunerResources/img/cards/skill.png";
        else if (type == CardType.POWER)
            return "tunerResources/img/cards/power.png";
        else
            return "tunerResources/img/cards/temp.png";
    }

    public static CardStrings rtCardStrings(String name) {
        return CardCrawlGame.languagePack.
                getCardStrings("tuner:" + name);
    }

    public void steal(AbstractCard c) {
        String img = c.assetUrl;
        this.portrait = MouldCard.cardAtlas.findRegion(img);
        this.assetUrl = img;
    }

    public void rewritingChange(AbstractCard c) {
    }

    @Override
    public abstract void use(AbstractPlayer p, AbstractMonster m);

    @Override
    public abstract void upgrade();

    private ArrayList<Texture> imgs = new ArrayList<>();
    private ArrayList<Integer> amounts = new ArrayList<>();
    private ArrayList<Color> colors = new ArrayList<>();

    private void renderNumber(SpriteBatch sb) {
        imgs.clear();
        amounts.clear();
        colors.clear();
        if (this.damage > 0) {
            imgs.add(FullArtSubscriber.getAttackImage(this.damage));
            amounts.add(this.damage);
            colors.add(Color.RED);
        }
        if (this.block > 0) {
            imgs.add(FullArtSubscriber.getDefendImage());
            amounts.add(this.block);
            colors.add(Settings.CREAM_COLOR);
        }
        if (this.baseMagicNumber > 0) {
            imgs.add(FullArtSubscriber.getMGCImage());
            amounts.add(this.baseMagicNumber);
            colors.add(Settings.BLUE_TEXT_COLOR);
        }

        float numberX = 87F;
        float numberY = 100;

        if (imgs.size() == 1) numberY = 100;
        if (imgs.size() == 2) numberY = 85;
        if (imgs.size() == 3) numberY = 75;

        int i = 0;

        for (Texture img : imgs) {
            int size = img.getWidth();
            ModHelper.renderRotateTexture(sb, img, this.current_x, this.current_y,
                    (numberX * Settings.scale - size / 2F * 0.75F) * drawScale, (-numberY * Settings.scale - size / 2F * 0.75F) * drawScale,
                    0.75F * this.drawScale, angle);

            FontHelper.cardTitleFont.getData().setScale(this.drawScale);
            FontHelper.renderRotatedText(sb, FontHelper.cardTitleFont, amounts.get(i).toString(),
                    this.current_x, this.current_y,
                    (numberX + 30) * Settings.scale * drawScale, -numberY * Settings.scale * drawScale,
                    this.angle, false,
                    colors.get(i));
            numberY += 25;
            i++;
        }
    }

    @SpireOverride
    protected void renderDescription(SpriteBatch sb) {
        if (!FullArtSubscriber.initShouldShowFullArt(this)) {
            SpireSuper.call(sb);
            return;
        }
        renderNumber(sb);
    }

    @SpireOverride
    protected void renderDescriptionCN(SpriteBatch sb) {
        if (!FullArtSubscriber.initShouldShowFullArt(this)) {
            SpireSuper.call(sb);
            return;
        }
        renderNumber(sb);
    }

    @SpireOverride
    protected void renderTitle(SpriteBatch sb) {
        if (!(this instanceof FullArtSubscriber && FullArtSubscriber.initShouldShowFullArt(this))) {
            SpireSuper.call(sb);
        } else {
            if (ReflectionHacks.getPrivate(this, AbstractCard.class, "useSmallTitleFont")) {
                FontHelper.cardTitleFont.getData().setScale(this.drawScale);
            } else {
                FontHelper.cardTitleFont.getData().setScale(this.drawScale * 0.85F);
            }

            if (this.upgraded) {
                Color color = Settings.GREEN_TEXT_COLOR.cpy();
                color.a = getRenderColor().a;
                FontHelper.renderRotatedText(sb, FontHelper.cardTitleFont, this.name, this.current_x, this.current_y, 0.0F, -100.0F * this.drawScale * Settings.scale, this.angle, false, color);
            } else {
                FontHelper.renderRotatedText(sb, FontHelper.cardTitleFont, this.name, this.current_x, this.current_y, 0.0F, -100.0F * this.drawScale * Settings.scale, this.angle, false, getRenderColor());
            }
        }
    }

    private Color getRenderColor() {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "renderColor");
    }

    private Color getTypeColor() {
        return ReflectionHacks.getPrivate(this, AbstractCard.class, "typeColor");
    }

    private void renderHelper(SpriteBatch sb, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY) {
        ReflectionHacks.privateMethod(AbstractCard.class, "renderHelper", new Class[]{SpriteBatch.class, Color.class, TextureAtlas.AtlasRegion.class, float.class, float.class}).invoke(this, new Object[]{sb, color, img, Float.valueOf(drawX), Float.valueOf(drawY)});
    }

    public void renderSmallEnergy(SpriteBatch sb, TextureAtlas.AtlasRegion region, float x, float y) {
        if (!(this instanceof FullArtSubscriber && FullArtSubscriber.initShouldShowFullArt(this))) {
            super.renderSmallEnergy(sb, region, x, y);
        }
    }

    @SpireOverride
    protected void renderCardBg(SpriteBatch sb, float x, float y) {
        if (this instanceof FullArtSubscriber) {
            if (!FullArtSubscriber.initShouldShowFullArt(this)) {
                SpireSuper.call(sb, x, y);
                //画小星星
                renderHelper(sb, getRenderColor(),
                        MyImageMaster.CardBgStarAtlas, x, y);
            }
        } else SpireSuper.call(sb, x, y);
    }

    @SpireOverride
    protected void renderPortrait(SpriteBatch sb) {
        if (FullArtSubscriber.initShouldShowFullArt(this)) {
            sb.setColor(getRenderColor());
            sb.draw(((FullArtSubscriber) this).getFullArtPortrait(), this.current_x - 256.0F, this.current_y - 256.0F, 256.0F, 256.0F, 512.0F, 512.0F, this.drawScale * Settings.scale, this.drawScale * Settings.scale, this.angle);
            renderHelper(sb, getRenderColor(),
                    FullArtSubscriber.getCardBg512(), this.current_x, this.current_y);
        } else {
            SpireSuper.call(sb);
        }
    }

    @SpireOverride
    protected void renderJokePortrait(SpriteBatch sb) {
        if (FullArtSubscriber.initShouldShowFullArt(this)) {
            renderPortrait(sb);
        } else {
            SpireSuper.call(sb);
        }
    }

    @SpireOverride
    protected void renderPortraitFrame(SpriteBatch sb, float x, float y) {
        if (!FullArtSubscriber.initShouldShowFullArt(this)) {
            SpireSuper.call(sb, Float.valueOf(x), Float.valueOf(y));
        }
    }

    @SpireOverride
    protected void renderBannerImage(SpriteBatch sb, float x, float y) {
        if (!FullArtSubscriber.initShouldShowFullArt(this))
            SpireSuper.call(sb, Float.valueOf(x), Float.valueOf(y));
    }

    @SpireOverride
    protected void renderType(SpriteBatch sb) {
        String text;
        if (!FullArtSubscriber.initShouldShowFullArt(this)) {
            SpireSuper.call(sb);
        }
    }

}

