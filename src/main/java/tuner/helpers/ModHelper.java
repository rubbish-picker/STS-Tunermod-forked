package tuner.helpers;

import basemod.abstracts.AbstractCardModifier;
import basemod.abstracts.CustomCard;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.interfaces.NonDiscardableSubscriber;
import tuner.modCore.CardColorEnum;
import tuner.relics.ATRelic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModHelper {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("TunerModHelper")).TEXT;
    public static final String ATTACK = "tunerResources/img/512/s/bg_attack_SSA_s.png";
    public static final String SKILL = "tunerResources/img/512/s/bg_skill_SSA_s.png";
    public static final String POWER = "tunerResources/img/512/s/bg_power_SSA_s.png";

    public static final String ATTACK_PORTRAIT = "tunerResources/img/1024/s/bg_attack_SSA.png";
    public static final String SKILL_PORTRAIT = "tunerResources/img/1024/s/bg_skill_SSA.png";
    public static final String POWER_PORTRAIT = "tunerResources/img/1024/s/bg_power_SSA.png";

    public static final Logger logger = LogManager.getLogger(ModHelper.class.getName());

    //安定变卡图背景
    public static void ChangeRewroteCardImg(AbstractCard c) {
        if (c instanceof CustomCard)
            switch (c.type) {
                case ATTACK:
                    ((CustomCard) c).setBackgroundTexture(ATTACK, ATTACK_PORTRAIT);
                    break;
                case SKILL:
                    ((CustomCard) c).setBackgroundTexture(SKILL, SKILL_PORTRAIT);
                    break;
                case POWER:
                    ((CustomCard) c).setBackgroundTexture(POWER, POWER_PORTRAIT);
                    break;
                default:
                    ((CustomCard) c).setBackgroundTexture(SKILL, SKILL_PORTRAIT);
                    break;
            }
    }

    //自己的随机数
    public static Random selfRamdom;

    public static void initSelfRamdom() {
        ModHelper.selfRamdom = new Random(Settings.seed + AbstractDungeon.floorNum * 10L);
    }

    //获得一张随机目标
    public static AbstractCard getRamdomTarget() {
        ArrayList<AbstractCard> tars = ModHelper.rtATgroup();
        return tars.get(selfRamdom.random(tars.size() - 1));
    }

    //添加id
    public static String makeID(String s) {
        return "tuner:" + s;
    }

    //获得遗物图片地址
    public static String makeRlcAd(String name, boolean isPortrait) {
        String isP = "32";
        if (isPortrait) isP = "84";

        return "tunerResources/img/powers/" + name + isP + ".png";
    }

    //判断毁灭者上是否有牌
    public static boolean canRewrote() {
        return AbstractDungeon.player != null &&
                ATRelic.at != null &&
                AbstractDungeon.player.hasRelic(ATRelic.ID) &&
                !AbstractDungeon.player.drawPile.isEmpty() &&
                AbstractDungeon.currMapNode != null &&
                AbstractDungeon.currMapNode.room != null &&
                AbstractDungeon.currMapNode.room.monsters != null &&
                ATRelic.at.MaxCount > 0;
    }


    //给神名碎片创造一个目标
    public static AbstractCreature rtTarget(AbstractCreature c) {
        if (c == null) {
            return AbstractDungeon.getRandomMonster();
        }
        return c;
    }

    //获得当前所有的目标
    public static ArrayList<AbstractCard> rtATgroup() {
        ArrayList<AbstractCard> list = new ArrayList<>();
        if (canRewrote()) {
            for (int i = AbstractDungeon.player.drawPile.size() - 1;
                 i >= AbstractDungeon.player.drawPile.size() - ATRelic.at.MaxCount && i >= 0;
                 i--) {
                list.add(0, AbstractDungeon.player.drawPile.group.get(i));
            }
        }
        return list;
    }

    //通用更新文本
    public static void initDes(AbstractCard c) {
        c.rawDescription = String.format(((MouldCard) c).DESCRIPTION, c.magicNumber);
        c.initializeDescription();
    }

    public static boolean imgUpgradeName(AbstractCard c) {
        if (c.timesUpgraded < 2) {
            c.timesUpgraded++;
            c.upgraded = true;
            if (c.timesUpgraded == 1)
                c.name = ((MouldCard) c).cardStrings.NAME + "+";
            else c.name = ((MouldCard) c).cardStrings.NAME + "++";
            return true;
        } else return false;
    }

    //返回一张随机的音符图片
    public static TextureRegion randomImg() {
        int roll = MathUtils.random(8);
        switch (roll) {
            case 0:
            case 1:
                return new TextureRegion(MyImageMaster.tuner1);
            case 2:
            case 3:
                return new TextureRegion(MyImageMaster.tuner2);
            case 4:
            case 5:
                return new TextureRegion(MyImageMaster.tuner3);
            case 6:
            case 7:
                return new TextureRegion(MyImageMaster.tuner4);
            case 8:
                return new TextureRegion(MyImageMaster.tuner5);
        }
        return new TextureRegion(MyImageMaster.tuner1);
    }

    public static Texture randomImg(boolean isTexture) {
        int roll = MathUtils.random(4);
        switch (roll) {
            case 0:
                return MyImageMaster.tuner1;
            case 1:
                return MyImageMaster.tuner2;
            case 2:
                return MyImageMaster.tuner3;
            case 3:
                return MyImageMaster.tuner4;
            case 4:
                return MyImageMaster.tuner5;
        }
        return MyImageMaster.tuner1;
    }

    //照抄的cardgroup的resetCardBeforeMoving
    public static void refreshCard(AbstractCard c) {
        if (AbstractDungeon.player.hoveredCard == c) {
            AbstractDungeon.player.releaseCard();
        }

        AbstractDungeon.actionManager.removeFromQueue(c);
        c.unhover();
        c.untip();
        c.stopGlowing();
        c.setAngle(0);
        c.flashVfx = null;
    }

    //可以选择音量大小地播放音乐
    public static long playSfx(String key, boolean useBgmVolume, float amp) {

        HashMap<String, Sfx> map;

        try {
            Field sfxmap = SoundMaster.class.getDeclaredField("map");
            sfxmap.setAccessible(true);
            map = (HashMap)sfxmap.get(CardCrawlGame.sound);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if(map != null) {
            if (CardCrawlGame.MUTE_IF_BG && Settings.isBackgrounded) {
                return 0L;
            } else if (map.containsKey(key)) {
                return useBgmVolume ?
                        map.get(key).play(Settings.MUSIC_VOLUME * Settings.MASTER_VOLUME * amp)
                        :
                        (map.get(key)).play(Settings.SOUND_VOLUME * Settings.MASTER_VOLUME * amp);
            }
        }
        ModHelper.logger.info("Music Missing: " + key);
        return 0L;
    }

    //从一个特定牌组装填一张牌
    public static void loadACard(AbstractCard c, CardGroup group) {
        refreshCard(c);
        if (group == null || group.contains(c)) {
            if (group != null)
                group.group.remove(c);

            if (AbstractDungeon.player.drawPile.size() < ATRelic.at.MaxCount) {
                AbstractDungeon.player.drawPile.addToBottom(c);
            } else if (AbstractDungeon.player.drawPile.size() == ATRelic.at.MaxCount) {
                AbstractDungeon.player.drawPile.addToBottom(c);
                deckMoveToHand(AbstractDungeon.player.drawPile.getTopCard());
            } else {
                AbstractDungeon.player.drawPile.group.add(
                        AbstractDungeon.player.drawPile.size() - ATRelic.at.MaxCount,
                        c);
                deckMoveToHand(AbstractDungeon.player.drawPile.getTopCard());

            }
        }
    }

    //是否可以刷出色图或者礼服皮肤
    public static boolean isSetuAvail() {
        return false;
//        return Loader.isModLoadedOrSideloaded("Blue archive Hina mod(dlc)");
    }

    //获得色图的颜色
    public static AbstractCard.CardColor rtDLCColor() {
        if (isSetuAvail())
            for (AbstractCard.CardColor c : basemod.BaseMod.getCardColors())
                if (c.name().equals("HinaDLC"))
                    return c;
        return null;
    }


    private static final Matrix4 mx4 = new Matrix4();
    private static final Matrix4 rotatedTextMatrix = new Matrix4();
    public static void renderRotateTexture(SpriteBatch sb, Texture t, float x, float y, float offsetX, float offsetY, float scale, float angle){
        mx4.setToRotation(0.0F, 0.0F, 1.0F, angle);

        Vector2 vec = new Vector2(offsetX, offsetY);
        ModHelper.rotate(vec, angle);
        mx4.trn(x + vec.x,
                y + vec.y, 0.0F);
        sb.end();
        sb.setTransformMatrix(mx4);
        sb.begin();
        sb.draw(t, 0, 0, 0, 0, t.getWidth(), t.getHeight(), scale, scale, 0,
                0, 0, t.getWidth(), t.getHeight(), false, false);
        sb.end();
        sb.setTransformMatrix(rotatedTextMatrix);
        sb.begin();
    }

    //旋转
    public static void rotate(Vector2 vec, float radians) {
        float cos = (float) Math.cos((double) radians * 0.017453292F);
        float sin = (float) Math.sin((double) radians * 0.017453292F);
        float newX = vec.x * cos - vec.y * sin;
        float newY = vec.x * sin + vec.y * cos;
        vec.x = newX;
        vec.y = newY;
    }

    public static void deckMoveToHand(AbstractCard c) {
        if (!AbstractDungeon.player.drawPile.contains(c)) return;

        if (AbstractDungeon.player.hand.size() >= 10) {
            AbstractDungeon.player.drawPile.moveToDiscardPile(c);
            AbstractDungeon.player.createHandIsFullDialog();
        } else {
            c.unhover();
            c.lighten(true);
            c.setAngle(0.0F);
            c.drawScale = 0.12F;
            c.targetDrawScale = 0.75F;
            c.current_x = CardGroup.DRAW_PILE_X;
            c.current_y = CardGroup.DRAW_PILE_Y;
            c.flash();
            AbstractDungeon.player.drawPile.removeCard(c);
            AbstractDungeon.player.hand.addToTop(c);
            AbstractDungeon.player.hand.refreshHandLayout();
            AbstractDungeon.player.hand.applyPowers();
        }
    }

    //丢弃MG上所有牌
    public static void discardAllCard() {
        if (ModHelper.canRewrote()) {
            for (AbstractCard c : rtATgroup()) {
                AbstractDungeon.player.drawPile.moveToDiscardPile(c);
            }
        }
    }

    //那些不移动到弃牌堆的牌的移动(0个用法是因为用字节码调用的)
    public static boolean onUsedCard(AbstractCard card) {
        if (card instanceof NonDiscardableSubscriber) {
            ((NonDiscardableSubscriber) card).onUseCard();
            return false;
        }
        return true;
    }


    //更新被安定牌上的神名碎片效果（已经用patch实现自动了）
    public static void CalculateEffect(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, "tuners:CalculateEffectMod")) {
            CardModifierManager.removeModifiersById(card, "tuners:CalculateEffectMod", true);
        }

        String txt = "";
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            String str = mod.identifier(card);
            if (str.contains("tuner:")) {
                txt = txt + ((AbstractMod) mod).owner.rawDescription + " NL ";
            }
        }
        if (!txt.isEmpty())
            CardModifierManager.addModifier(card, new CalculateEffectMod(txt));
    }

    private static class CalculateEffectMod extends AbstractCardModifier {
        public String text;

        public CalculateEffectMod(String text) {
            this.text = text;
        }

        @Override
        public boolean isInherent(AbstractCard card) {
            return true;
        }

        @Override
        public String identifier(AbstractCard card) {
            return "tuners:CalculateEffectMod";
        }

        @Override
        public List<TooltipInfo> additionalTooltips(AbstractCard card) {
            ArrayList<TooltipInfo> tips = new ArrayList<>();
            tips.add(new TooltipInfo(TEXT[0], text));
            return tips;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new CalculateEffectMod(text);
        }
    }

}