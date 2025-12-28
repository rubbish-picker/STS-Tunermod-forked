package tuner.characters;

import basemod.abstracts.CustomEnergyOrb;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.RestRoom;
import tuner.action.MessageCaller;
import tuner.cards.Reconcile;
import tuner.helpers.ConfigHelper;
import tuner.helpers.ModHelper;
import tuner.helpers.ModelController.*;
import tuner.misc.charSelect.SkinSelectScreen;
import tuner.helpers.MyImageMaster;
import tuner.modCore.CardColorEnum;
import tuner.modCore.PlayerEnum;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import tuner.patches.combat.EndTurnButtonPatch;
import tuner.relics.ATRelic;
import tuner.relics.MemoryFragments;

import java.util.ArrayList;
import java.util.List;

import static tuner.helpers.ConfigHelper.activeTutorials;

public class Tuner extends CustomPlayer {
    private static final int ENERGY_PER_TURN = 3;

    private static final TutorialStrings README = CardCrawlGame.languagePack.getTutorialString("TunerREADME");
    private static final TutorialStrings BeforeDead = CardCrawlGame.languagePack.getTutorialString("TunerBeforeDead");
    private static final String SHOULDER_2 = "tunerResources/img/char/shoulder2.png";
    private static final String SHOULDER_1 = "tunerResources/img/char/shoulder1.png";
    private static final String CORPSE = "tunerResources/img/char/HinaDie.png";


    private static final String[] ORB_TEXTURES = new String[]{
            "tunerResources/img/UI/EPanel/day/5.png", "tunerResources/img/UI/EPanel/day/4.png", "tunerResources/img/UI/EPanel/day/3.png", "tunerResources/img/UI/EPanel/day/2.png", "tunerResources/img/UI/EPanel/day/1.png",
            "tunerResources/img/UI/EPanel/01.png",
            "tunerResources/img/UI/EPanel/day/5.png", "tunerResources/img/UI/EPanel/day/4.png", "tunerResources/img/UI/EPanel/day/3.png", "tunerResources/img/UI/EPanel/day/2.png", "tunerResources/img/UI/EPanel/day/1.png"};
    private static final String[] ORB_TEXTURES2 = new String[]{
            "tunerResources/img/UI/EPanel/night/5.png", "tunerResources/img/UI/EPanel/night/4.png", "tunerResources/img/UI/EPanel/night/3.png", "tunerResources/img/UI/EPanel/night/2.png", "tunerResources/img/UI/EPanel/night/1.png",
            "tunerResources/img/UI/EPanel/02.png",
            "tunerResources/img/UI/EPanel/night/5.png", "tunerResources/img/UI/EPanel/night/4.png", "tunerResources/img/UI/EPanel/night/3.png", "tunerResources/img/UI/EPanel/night/2.png", "tunerResources/img/UI/EPanel/night/1.png",};
    private static final String ORB_VFX = "tunerResources/img/UI/energyBlueVFX.png";
    private static final float[] LAYER_SPEED = new float[]{
            -20F, 30F, -1F, 8F, 0F,
            -20F, 30F, -1F, 8F, 0F};
    private static final float[] LAYER_SPEED2 = new float[]{
            -1F, 8F, 0F, 0F, 0F,
            -1F, 8F, 0F, 0F, 0F};
    private static final int STARTING_HP = 65;
    private static final int MAX_HP = 65;
    private static final int STARTING_GOLD = 120;
    private static final int DRAW_SIZE = 5;
    private static final int ASCENSION_MAX_HP_LOSS = 6;

    public static final Color Tuner_COLOR = CardHelper.getColor(191, 64, 191);

    public character3DHelper char3D;
    public static String[] stand2D = {
            "tunerResources/img/char/Hina.png",
            "tunerResources/img/char/null.png"
    };
    public int skinType = -1;

    private final CustomEnergyOrb energyOrb2;

    private boolean RclickStart = false;
    private boolean Rclick = false;
    private float dragX = 0;
    private float dragY = 0;


    public Tuner(String name) {

        super(name, PlayerEnum.Tuner_CLASS, ORB_TEXTURES, ORB_VFX, LAYER_SPEED, null, null);
        this.dialogX = this.drawX + 0.0F * Settings.scale;
        this.dialogY = this.drawY + 220.0F * Settings.scale;

        initializeClass(stand2D[0], SHOULDER_2, SHOULDER_1, CORPSE,
                getLoadout(),
                0F, 5.0F, 240.0F, 300.0F,
                new EnergyManager(ENERGY_PER_TURN));

        this.energyOrb2 = new CustomEnergyOrb(ORB_TEXTURES2, ORB_VFX, LAYER_SPEED2);
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        //添加初始卡组
        ArrayList<String> retVal = new ArrayList<>();

        retVal.add("tuner:TunerStrike");
        retVal.add("tuner:TunerStrike");
        retVal.add("tuner:TunerStrike");
        retVal.add("tuner:TunerStrike");
        retVal.add("tuner:TunerDefend");
        retVal.add("tuner:TunerDefend");
        retVal.add("tuner:TunerDefend");
        retVal.add("tuner:TunerDefend");

        retVal.add("tuner:Reconcile");
        retVal.add("tuner:Deviator");

        retVal.add("tuner:Strike");
        retVal.add("tuner:Strike");
        retVal.add("tuner:Defence");
        retVal.add("tuner:Defence");

        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        //添加初始遗物
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(MemoryFragments.ID);
        UnlockTracker.markRelicAsSeen(MemoryFragments.ID);
        retVal.add(ATRelic.ID);
        UnlockTracker.markRelicAsSeen(ATRelic.ID);
        return retVal;
    }

    @Override
    public CharSelectInfo getLoadout() {
        String title = README.LABEL[0];
        String flavor = README.TEXT[0];

        return new CharSelectInfo(
                title, // 人物名字
                flavor, // 人物介绍
                STARTING_HP, // 当前血量
                MAX_HP, // 最大血量
                0, // 初始充能球栏位
                STARTING_GOLD, // 初始携带金币
                DRAW_SIZE, // 每回合抽牌数量
                this, // 别动
                this.getStartingRelics(), // 初始遗物
                this.getStartingDeck(), // 初始卡组
                false // 别动
        );
    }


    @Override
    public String getTitle(PlayerClass playerClass) {

        String title = "";
        if (Settings.language == Settings.GameLanguage.ZHS) {
            title = "日奈";
        } else if (Settings.language == Settings.GameLanguage.ZHT) {
            title = "日奈";
        } else {
            title = "Hina";
        }

        return title;
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        //选择卡牌颜色
        return CardColorEnum.TunerColor;
    }

    @Override
    public Color getCardRenderColor() {
        return Tuner_COLOR;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new Reconcile();
    }

    @Override
    public Color getCardTrailColor() {
        return Tuner_COLOR;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return ASCENSION_MAX_HP_LOSS;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        int roll = MathUtils.random(2);
        switch (roll) {
            case 0:
                CardCrawlGame.sound.playA("HINA_OPEN1", 0);
                break;
            case 1:
                CardCrawlGame.sound.playA("HINA_OPEN2", 0);
                break;
            case 2:
                CardCrawlGame.sound.playA("HINA_OPEN3", 0);
                break;
        }
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, true);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "SELECT_SSA";
    }

    @Override
    public String getLocalizedCharacterName() {
        String char_name;
        if (Settings.language == Settings.GameLanguage.ZHS) {
            char_name = "日奈";
        } else if (Settings.language == Settings.GameLanguage.ZHT) {
            char_name = "日奈";
        } else {
            char_name = "Hina";
        }
        return char_name;
    }

    @Override
    public AbstractPlayer newInstance() {
        return new Tuner(this.name);
    }

    @Override
    public String getSpireHeartText() {
        return BeforeDead.TEXT[0];
    }

    @Override
    public Color getSlashAttackColor() {
        return Tuner_COLOR;
    }


    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL, AbstractGameAction.AttackEffect.SLASH_HEAVY, AbstractGameAction.AttackEffect.FIRE, AbstractGameAction.AttackEffect.SLASH_DIAGONAL};
    }

    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[1];
    }

    @Override
    public ArrayList<CutscenePanel> getCutscenePanels() {
        ArrayList<CutscenePanel> panels = new ArrayList<>();
        // 有两个参数的，第二个参数表示出现图片时播放的音效
        panels.add(new CutscenePanel("tunerResources/img/event/epcg/cut1.png"));
        panels.add(new CutscenePanel("tunerResources/img/event/epcg/cut2.png"));
        panels.add(new CutscenePanel("tunerResources/img/event/epcg/cut3.png"));

        return panels;
    }

    @Override
    public void applyStartOfTurnPostDrawRelics() {
        super.applyStartOfTurnPostDrawRelics();
        //加载教程
        if (activeTutorials) {
            AbstractDungeon.actionManager.addToBottom(new MessageCaller(0));
            AbstractDungeon.actionManager.addToBottom(new MessageCaller(1));
            AbstractDungeon.actionManager.addToBottom(new MessageCaller(2));
            AbstractDungeon.actionManager.addToBottom(new MessageCaller(3));
        }
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y) {
        if (EndTurnButtonPatch.ended) {
            this.energyOrb2.updateOrb(3);
            this.energyOrb2.renderOrb(sb, enabled, current_x, current_y);
        } else
            this.energyOrb.renderOrb(sb, enabled, current_x, current_y);
    }

    @Override
    public void useCard(AbstractCard c, AbstractMonster monster, int energyOnUse) {
        applyUseCardAni(c, monster);
        super.useCard(c, monster, energyOnUse);
    }

    private void applyUseCardAni(AbstractCard c, AbstractMonster monster) {
        ArrayList<AnimaItem> list = new ArrayList<>();
        if (skinType == 0) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                if (c.costForTurn < 2) {
                    list.add(HinaAnimaItem.ATTACKING);
                    list.add(HinaAnimaItem.ATTACK_END);
                    char3D.queueAnimaItem(list, !char3D.isAttacking());
                } else {
                    list.add(HinaAnimaItem.ATTACK_START);
                    list.add(HinaAnimaItem.ATTACKING);
                    list.add(HinaAnimaItem.ATTACK_END);
                    char3D.queueAnimaItem(list, true);
                }
            }
            if (c.type == AbstractCard.CardType.POWER) {
                list.add(HinaAnimaItem.RELOAD);
                char3D.queueAnimaItem(list, true);
            }
        }
        if (skinType == 1) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                if (c.costForTurn < 2) {
                    list.add(HinaSwimAnimaItem.ATTACKING);
                    list.add(HinaSwimAnimaItem.ATTACK_END);
                    char3D.queueAnimaItem(list, !char3D.isAttacking());
                } else {
                    list.add(HinaSwimAnimaItem.ATTACK_START);
                    list.add(HinaSwimAnimaItem.ATTACKING);
                    list.add(HinaSwimAnimaItem.ATTACK_END);
                    char3D.queueAnimaItem(list, true);
                }
            }
            if (c.type == AbstractCard.CardType.POWER) {
                list.add(HinaSwimAnimaItem.RELOAD);
                char3D.queueAnimaItem(list, true);
            }
        }
        if (skinType == 2) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                if (c.costForTurn < 2) {
                    list.add(HinaDressAnimaItem.ATTACKING);
                    list.add(HinaDressAnimaItem.ATTACK_END);
                    char3D.queueAnimaItem(list, !char3D.isAttacking());
                } else {
                    list.add(HinaDressAnimaItem.ATTACK_START);
                    list.add(HinaDressAnimaItem.ATTACKING);
                    list.add(HinaDressAnimaItem.ATTACK_END);
                    char3D.queueAnimaItem(list, true);
                }
            }
            if (c.type == AbstractCard.CardType.POWER) {
                list.add(HinaDressAnimaItem.RELOAD);
                char3D.queueAnimaItem(list, true);
            }
        }
    }

    private boolean heavyAttackEffectAvail = true;

    @Override
    public void update() {
        super.update();

        updateSkinType();
        if (skinType != -1) {
            updateReaction();

            if (char3D == null)
                try {
                    char3D = new character3DHelper();
                } catch (Exception e) {
                    ModHelper.logger.warn(e);
                    ConfigHelper.dontLoad3d = true;
                    skinType = -1;
                }

            char3D.update(this.dragX, this.dragY);
        }


//        if (char3D != null && char3D.isAttacking()) {
//            if (heavyAttackEffectAvail) {
//                heavyAttackEffectAvail = false;
//                if (skinType == 0)
//                    AbstractDungeon.effectsQueue.add(new HeavyArtilleryEffect(1F));
//                if (skinType == 1)
//                    AbstractDungeon.effectsQueue.add(new HeavyArtilleryEffect(0.1F));
//            }
//        } else {
//            heavyAttackEffectAvail = true;
//        }
    }

    private void updateReaction() {
        if (this.hb != null && this.hb.hovered && InputHelper.justClickedRight) {
            this.RclickStart = true;
            //设置动画
            ArrayList<AnimaItem> list = new ArrayList<>();
            if (skinType == 0) {
                CardCrawlGame.sound.play("PICKUP0");
                list.add(HinaAnimaItem.Formation_Pickup);
            }
            if (skinType == 1) {
                CardCrawlGame.sound.play("PICKUP1");
                list.add(HinaSwimAnimaItem.Formation_Pickup);
            }
            if (skinType == 2) {
                CardCrawlGame.sound.play("PICKUP2");
                list.add(HinaDressAnimaItem.Formation_Pickup);
            }
            char3D.queueAnimaItem(list, true, -1);
            char3D.rotateModel(0, 1, 0, -90);
        }

        if (this.RclickStart && InputHelper.justReleasedClickRight) {
            this.RclickStart = false;
            //停止动画
            char3D.clearAnimation();
        }

        if (this.RclickStart) {
            this.dragX = InputHelper.mX;
            this.dragY = InputHelper.mY - 50 * Settings.scale;
        } else {
            this.dragX = this.drawX;
            this.dragY = this.drawY;
        }
    }

    private int formalSkinType = -2;

    private void updateSkinType() {
        int i = SkinSelectScreen.Inst.index;

        if (ConfigHelper.dontLoad3d) {
            skinType = -1;
        } else {
            skinType = i;
        }

        if (formalSkinType != skinType) {
            formalSkinType = skinType;
            if (skinType != -1) {
                this.img = ImageMaster.loadImage(stand2D[1]);
                this.corpseImg = ImageMaster.loadImage(stand2D[1]);
            } else {
                this.img = ImageMaster.loadImage(stand2D[0]);
                this.corpseImg = ImageMaster.loadImage(CORPSE);
            }
        }
    }

    @Override
    public void playDeathAnimation() {
        if (skinType != -1) {
            List<AnimaItem> items = new ArrayList<>();
            if (skinType == 0)
                items.add(HinaAnimaItem.DYING);

            if (skinType == 1)
                items.add(HinaSwimAnimaItem.DYING);

            if (skinType == 2)
                items.add(HinaDressAnimaItem.DYING);

            this.char3D.queueAnimaItem(items, false, -1);
        } else {
            super.playDeathAnimation();
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (skinType != -1 && !(AbstractDungeon.getCurrRoom() instanceof RestRoom)) {
            if (this.isDead)
                char3D.update(this.drawX, this.drawY);
            spriteBatch.setColor(Color.WHITE);
//            spriteBatch.draw(MyImageMaster.charShadow, this.drawX - MyImageMaster.charShadow.getWidth()* Settings.scale/2F, this.drawY - MyImageMaster.charShadow.getHeight()* Settings.scale/2F);
            spriteBatch.draw(MyImageMaster.charShadow,
                    this.drawX - MyImageMaster.charShadow.getWidth() * Settings.scale / 2.0F,
                    this.drawY - MyImageMaster.charShadow.getHeight() * Settings.scale / 2.0F,
                    MyImageMaster.charShadow.getWidth() * Settings.scale, MyImageMaster.charShadow.getHeight() * Settings.scale,
                    0, 0,
                    MyImageMaster.charShadow.getWidth(), MyImageMaster.charShadow.getHeight(),
                    this.flipHorizontal, this.flipVertical);
            char3D.render(spriteBatch, this.flipHorizontal);
        }
        super.render(spriteBatch);
    }
}

