package tuner.modCore;

import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.ReflectionHacks;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.audio.Sfx;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.action.RewriteAction;
import tuner.cards.*;
import tuner.cards.colorless.*;
import tuner.cards.imaginaryColor.*;
import tuner.characters.Tuner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tuner.effects.ShowATEffect;
import tuner.helpers.ConfigHelper;
import tuner.helpers.RandomImgCard;
import tuner.misc.charSelect.SkinSelectScreen;
import tuner.misc.CustomSave;
import tuner.misc.MapcardTarget;
import tuner.misc.PIDStrings;
import tuner.misc.ImaginaryReward;
import tuner.helpers.ModHelper;
import tuner.events.TunerSampleEvent;
import tuner.events.LLMGeneratedEvent;
import tuner.llm.LLMEventService;
import tuner.patches.combat.DrawPileMonitor;
import tuner.patches.combat.EndTurnButtonPatch;
import tuner.patches.utils.RightClickPatch;
import tuner.powers.FusionPower;
import tuner.relics.ATRelic;
import tuner.relics.Bless;
import tuner.relics.GehennaPrefectTeam;
import tuner.relics.MemoryFragments;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static tuner.cards.Mapping.qianghuaTurn;
import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardColorEnum.TunerColor;
import static tuner.modCore.CardTargetEnum.MapCard;

@SpireInitializer
public class AstrographTuner implements
        PostPowerApplySubscriber,
        PostExhaustSubscriber,
//PostBattleSubscriber, 
        PostDungeonInitializeSubscriber,
        PostInitializeSubscriber,
        EditCharactersSubscriber,
        EditRelicsSubscriber,
        EditCardsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        OnCardUseSubscriber,
        OnPowersModifiedSubscriber,
        PostDrawSubscriber,
        PostEnergyRechargeSubscriber,
        OnPlayerDamagedSubscriber,
        OnPlayerTurnStartSubscriber,
        AddAudioSubscriber,
        RelicGetSubscriber,
        PostRenderSubscriber,
        PostUpdateSubscriber,
        OnStartBattleSubscriber,
        PostBattleSubscriber {
    public static final Logger logger = LogManager.getLogger(AstrographTuner.class.getName());

    //private static final String MOD_BADGE = "img/UI/badge.png";
    public static final String ATTACK = "tunerResources/img/512/bg_attack_temp.png";
    public static final String SKILL = "tunerResources/img/512/bg_skill_temp.png";
    public static final String POWER = "tunerResources/img/512/bg_power_temp.png";

    public static final String ATTACK_PORTRAIT = "tunerResources/img/1024/bg_attack_temp.png";
    public static final String SKILL_PORTRAIT = "tunerResources/img/1024/bg_skill_temp.png";
    public static final String POWER_PORTRAIT = "tunerResources/img/1024/bg_power_temp.png";

    public static final String ENERGY_ORB = "tunerResources/img/512/HinaCardOrb.png";
    public static final String ENERGY_ORB_PORTRAIT = "tunerResources/img/1024/HinaCardOrb.png";
    public static final String CARD_ENERGY_ORB = "tunerResources/img/UI/SSAEnergyOrb.png";

    //神名碎片的能量
    public static final String SKILL2 = "tunerResources/img/512/bg_skill_img.png";
    public static final String SKILL_PORTRAIT2 = "tunerResources/img/1024/bg_skill_img.png";
    public static final String ENERGY_ORB2 = "tunerResources/img/512/ImgCardOrb.png";
    public static final String ENERGY_ORB_PORTRAIT2 = "tunerResources/img/1024/ImgCardOrb.png";
    public static final String CARD_ENERGY_ORB2 = "tunerResources/img/UI/SSAEnergyOrb.png";

    private static final String BUTTON = "tunerResources/img/charSelect/Button.png";
    private static final String SMALL_BUTTON = "tunerResources/img/charSelect/smallButton.png";
    private static final String PORTRAIT = "tunerResources/img/charSelect/Portrait.jpg";
    public static final Color Purple = CardHelper.getColor(191, 64, 191);

    private ArrayList<AbstractCard> cardsToAdd = new ArrayList<>();

    public static int TurnCounter;

    private static int lastSeenFloorForLLMPrefetch = -1;

    public AstrographTuner() {
        BaseMod.subscribe(this);
        BaseMod.addColor(TunerColor, Purple, Purple, Purple, Purple, Purple, Purple, Purple, ATTACK, SKILL, POWER, ENERGY_ORB, ATTACK_PORTRAIT, SKILL_PORTRAIT, POWER_PORTRAIT, ENERGY_ORB_PORTRAIT, CARD_ENERGY_ORB);
        BaseMod.addColor(ImaginaryColor, Purple, Purple, Purple, Purple, Purple, Purple, Purple, ATTACK, SKILL2, POWER, ENERGY_ORB2, ATTACK_PORTRAIT, SKILL_PORTRAIT2, POWER_PORTRAIT, ENERGY_ORB_PORTRAIT2, CARD_ENERGY_ORB);
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new Tuner("Hina"), BUTTON, PORTRAIT, PlayerEnum.Tuner_CLASS, SMALL_BUTTON);
    }

    public static void initialize() {
        new AstrographTuner();

        ConfigHelper.tryCreateConfig();
    }

    @Override
    public void receiveEditCards() {
        logger.info("================加入卡牌=============");

        (new AutoAdd("Blue archive Hina mod"))
                .packageFilter("tuner.cards")
                .any(AbstractCard.class, (info, c) -> {
                    BaseMod.addCard(c);
                    UnlockTracker.unlockCard(c.cardID);
                });

        logger.info("================加入卡牌=============");
    }

    private static String loadJson(String jsonPath) {
        return Gdx.files.internal(jsonPath).readString(String.valueOf(StandardCharsets.UTF_8));
    }

    @Override
    public void receiveEditKeywords() {
        String keywordsPath = "";
        logger.info("===============加载关键字===============");

        if (Settings.language == Settings.GameLanguage.ZHS || Settings.language == Settings.GameLanguage.ZHT) {
            keywordsPath = "tunerResources/localization/tuner_keywords_zh.json";
        } else if (Settings.language == Settings.GameLanguage.KOR)
            keywordsPath = "tunerResources/localization/tuner_keywords_kr.json";
        else {
            keywordsPath = "tunerResources/localization/tuner_keywords_eng.json";
        }

        Gson gson = new Gson();

        Keywords keywords = (Keywords) gson.fromJson(loadJson(keywordsPath), Keywords.class);
        for (Keyword key : keywords.keywords) {
            logger.info("Loading keyword : " + key.NAMES[0]);
            BaseMod.addKeyword(key.NAMES, key.DESCRIPTION);
        }
        logger.info("===============加载关键字===============");
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio("REST0_1", "audio/hina/Hina_Cafe_Act_1.ogg");
        BaseMod.addAudio("REST0_2", "audio/hina/Hina_Cafe_Act_2.ogg");
        BaseMod.addAudio("REST0_3", "audio/hina/Hina_Cafe_Act_3.ogg");
        BaseMod.addAudio("REST0_4", "audio/hina/Hina_Cafe_Act_4.ogg");
        BaseMod.addAudio("REST0_5", "audio/hina/Hina_Cafe_Act_5.ogg");
        BaseMod.addAudio("HINA_OPEN1", "audio/hina/opening.mp3");
        BaseMod.addAudio("PICKUP0", "audio/hina/Hina_Formation_Select.ogg");

        BaseMod.addAudio("REST1_1", "audio/hinaswim/CH0063_Cafe_monolog_1.ogg");
        BaseMod.addAudio("REST1_2", "audio/hinaswim/CH0063_Cafe_monolog_2.ogg");
        BaseMod.addAudio("REST1_3", "audio/hinaswim/CH0063_Cafe_monolog_3.ogg");
        BaseMod.addAudio("REST1_4", "audio/hinaswim/CH0063_Cafe_monolog_4.ogg");
        BaseMod.addAudio("REST1_5", "audio/hinaswim/CH0063_Cafe_monolog_5.ogg");
        BaseMod.addAudio("HINA_OPEN2", "audio/hinaswim/opening.mp3");
        BaseMod.addAudio("PICKUP1", "audio/hinaswim/CH0063_Formation_Select.ogg");

        BaseMod.addAudio("REST2_1", "audio/hinadressed/CH0230_Cafe_monolog_1.ogg");
        BaseMod.addAudio("REST2_2", "audio/hinadressed/CH0230_Cafe_monolog_2.ogg");
        BaseMod.addAudio("REST2_3", "audio/hinadressed/CH0230_Cafe_monolog_3.ogg");
        BaseMod.addAudio("REST2_4", "audio/hinadressed/CH0230_Cafe_monolog_4.ogg");
        BaseMod.addAudio("REST2_5", "audio/hinadressed/CH0230_Cafe_monolog_5.ogg");
        BaseMod.addAudio("HINA_OPEN3", "audio/hinadressed/opening.mp3");
        BaseMod.addAudio("PICKUP2", "audio/hinadressed/CH0230_Formation_Select.ogg");

        BaseMod.addAudio("tunerShoot1", "audio/sfx/shoot/1.mp3");
        BaseMod.addAudio("tunerShoot2", "audio/sfx/shoot/2.mp3");
        BaseMod.addAudio("tunerShoot3", "audio/sfx/shoot/3.mp3");
        BaseMod.addAudio("tunerShoot4", "audio/sfx/shoot/4.mp3");
        BaseMod.addAudio("tunerTriShoot", "audio/sfx/shoot/tri.mp3");
        BaseMod.addAudio("tunerTunerShoot1", "audio/sfx/tuner/1.wav");
        BaseMod.addAudio("tunerTunerShoot2", "audio/sfx/tuner/2.wav");
        BaseMod.addAudio("tunerTunerShoot3", "audio/sfx/tuner/3.wav");
        BaseMod.addAudio("tunerLongShoot1", "audio/sfx/longshot1.wav");
        BaseMod.addAudio("tunerLongShoot2", "audio/sfx/longshot2.wav");
        BaseMod.addAudio("HighDimensionalExistence", "audio/sfx/SFX_CH0230_Public.mp3");
        BaseMod.addAudio("Piaofu", "audio/sfx/SFX_Azusa_Swimsuit_Public.mp3");
        BaseMod.addAudio("tunerShoot5s", "audio/sfx/SFX_Skill_Azusa_Ex_1.wav");
        BaseMod.addAudio("tunerForm", "audio/sfx/SFX_Ako_Cutin.wav");

//        这个写到post里去了
//        (new AutoAdd("Blue archive Hina mod"))
//                .packageFilter("tuner.cards.imaginaryColor")
//                .any(MouldCard.class, (info, c) -> {
//                    String s = c.getClass().getSimpleName();
//                    String path = "audio/img/" + s + ".ogg";
//                    if (Gdx.files.internal(path).exists()){
//                        BaseMod.addAudio(s, path);
//                    }
//                });
    }

    @Override
    public void receiveEditStrings() {

        String relic = "", card = "", power = "", potion = "", event = "", ui = "", tutorial = "", monster = "";
        logger.info("===============加载文字信息===============");

        String lang;
        if (Settings.language == Settings.GameLanguage.ZHS || Settings.language == Settings.GameLanguage.ZHT) {
            lang = "zh";
        } else if (Settings.language == Settings.GameLanguage.KOR)
            lang = "kr";
        else lang = "eng";

        //config在下面init setting里单独判断
        //keywords在receiveEditKeywords里单独判断
        card = "tunerResources/localization/tuner_cards_" + lang + ".json";
        relic = "tunerResources/localization/tuner_relics_" + lang + ".json";
        power = "tunerResources/localization/tuner_powers_" + lang + ".json";
        //potion = "localization/ThMod_YM_potions-zh.json";
        event = "tunerResources/localization/tuner_event_" + lang + ".json";
        ui = "tunerResources/localization/tuner_ui_" + lang + ".json";
        tutorial = "tunerResources/localization/tuner_tutorial_" + lang + ".json";
        monster = "tunerResources/localization/tuner_monsters_" + lang + ".json";


        String relicStrings = Gdx.files.internal(relic).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);

        String cardStrings = Gdx.files.internal(card).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
        PIDStrings.parseAndStore(cardStrings);

        String powerStrings = Gdx.files.internal(power).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
        //     String potionStrings = Gdx.files.internal(potion).readString(String.valueOf(StandardCharsets.UTF_8));
        //     BaseMod.loadCustomStrings(PotionStrings.class, potionStrings);
        String eventStrings = Gdx.files.internal(event).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(EventStrings.class, eventStrings);

        String uiStrings = Gdx.files.internal(ui).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);

        String tutorialStrings = Gdx.files.internal(tutorial).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(TutorialStrings.class, tutorialStrings);

        String monsterStrings = Gdx.files.internal(monster).readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(MonsterStrings.class, monsterStrings);
        logger.info("===============加载文字信息===============");
    }


    @Override
    public void receivePostInitialize() {
        logger.info("===============加载事件与其他东西===============");
//        BaseMod.addEvent("MetAlbaz", MetAlbaz.class, "TheBeyond");
//        BaseMod.addMonster("zhenyan", () -> new monsters.Zhenyan());

        BaseMod.addEvent(TunerSampleEvent.ID, TunerSampleEvent.class, "Exordium");
        // Register LLM generated event to all acts
        BaseMod.addEvent(LLMGeneratedEvent.ID, LLMGeneratedEvent.class, "Exordium");
        BaseMod.addEvent(LLMGeneratedEvent.ID, LLMGeneratedEvent.class, "TheCity");
        BaseMod.addEvent(LLMGeneratedEvent.ID, LLMGeneratedEvent.class, "TheBeyond");

        SkinSelectScreen.Inst = new SkinSelectScreen();

        BaseMod.registerCustomReward(
                RewardTypeEnum.Tuner_HPREWARD,
                (rewardSave) -> { // this handles what to do when this quest type is loaded.
                    return new ImaginaryReward(rewardSave.amount);
                },
                (customReward) -> { // this handles what to do when this quest type is saved.
                    return new RewardSave(customReward.type.toString(), null, ((ImaginaryReward) customReward).amount, 0);
                });
        CustomTargeting.registerCustomTargeting(MapCard, new MapcardTarget());

        RandomImgCard.init();

//        ShijianHelper.giveMeALike();
        logger.info("===============加载事件与其他东西===============");

//        加载config按钮
        ModPanel settingsPanel = ConfigHelper.initSettings(true);
        Texture badgeTexture = ImageMaster.loadImage("tunerResources/img/UI/badge.png");
        BaseMod.registerModBadge(badgeTexture, "Blue archive Hina mod", "fang fifth", "", settingsPanel);
        BaseMod.addSaveField("tuner:save", new CustomSave());


//        添加音效
        HashMap<String, Sfx> map = ReflectionHacks.getPrivate(CardCrawlGame.sound, SoundMaster.class, "map");
        if (map != null) {
            (new AutoAdd("Blue archive Hina mod"))
                    .packageFilter("tuner.cards.imaginaryColor")
                    .any(MouldCard.class, (info, c) -> {
                        String s = c.getClass().getSimpleName();
                        String path = "audio/img/" + s + ".ogg";
                        if (Gdx.files.internal(path).exists()) {
                            map.put("Hina_" + s, new Sfx(path));
                        }
                    });
        } else {
            logger.warn("Unexpectedly failed to add sounds.");
        }
        ReflectionHacks.setPrivate(CardCrawlGame.sound, SoundMaster.class, "map", map);
    }

    @Override
    public void receiveEditRelics() {
        logger.info("===============加载遗物===============");
        BaseMod.addRelicToCustomPool(new MemoryFragments(), TunerColor);
        BaseMod.addRelicToCustomPool(new GehennaPrefectTeam(), TunerColor);
        BaseMod.addRelic(new ATRelic(), RelicType.SHARED);
        BaseMod.addRelic(new Bless(), RelicType.SHARED);
        logger.info("===============加载遗物===============");
    }

    @Override
    public void receivePostDungeonInitialize() {
        if (ConfigHelper.otherObtainDestroyer && !(AbstractDungeon.player instanceof Tuner)) {
            if (!AbstractDungeon.player.hasRelic(ATRelic.ID)) {
                new ATRelic().instantObtain();
            }

            AbstractCard c = new AorB();
            UnlockTracker.markCardAsSeen(c.cardID);
            AbstractDungeon.player.masterDeck.addToBottom(c);
            AbstractDungeon.player.masterDeck.addToBottom(new Defence());
            AbstractDungeon.player.masterDeck.addToBottom(new Defence());
            AbstractDungeon.player.masterDeck.addToBottom(new Strike());
            AbstractDungeon.player.masterDeck.addToBottom(new Strike());
        }
    }

    @Override
    public void receivePostEnergyRecharge() {
    }

    @Override
    public void receiveRelicGet(AbstractRelic relic) {
    }

    @Override
    public void receiveCardUsed(AbstractCard c) {

        //映射效果
        if (TurnCounter <= qianghuaTurn && AstrographTuner.qianghua.get(c.cardID) != null) {
            AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(AstrographTuner.qianghua.get(c.cardID)));
        }

        //唤雷者效果
        if (AbstractDungeon.player.hasRelic(ATRelic.ID)) {
            for (AbstractCard card : ModHelper.rtATgroup()) {
                if (card instanceof SummonerOfThunder) {
                    card.use(null, null);
                }
            }
        }

        //校正弹效果
        if (c instanceof SpottingRound) {
            ((SpottingRound) c).check();
        }

    }

    public static HashMap<String, Integer> qianghua = new HashMap<>();

    @Override
    public void receiveOnBattleStart(AbstractRoom r) {
        //关视频（试图让视频不卡）
        if (SkinSelectScreen.Inst.playVideoScreen != null)
            SkinSelectScreen.Inst.playVideoScreen.over(true);

        //重置改写框框的状态
        RewriteAction.Rewriting = false;

        //初始化重关的计数
        FusionPower.IDIndex = 0;

        //初始化音符舞的patch的初始数值
        Fate.updatePatch.count = 0;

        //初始化自己的随机数
        ModHelper.initSelfRamdom();

        //初始化回合计数器
        TurnCounter = 0;

        //初始化Drawpile Monitor
        DrawPileMonitor.CardIsDrawn = new HashSet<>();
        DrawPileMonitor.newList = new HashSet<>();
        DrawPileMonitor.newList.addAll(AbstractDungeon.player.drawPile.group);

        //音符舞动画秒杀重置
        Fate.triggerAni = false;

        //刷新卡牌的效果
        qianghua = new HashMap<>();

        for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
            if (c instanceof Daedalus) c.applyPowers();
            if (c instanceof Eurora) ((Eurora) c).atBattleStart();
            if (c instanceof Mapping) ((Mapping) c).atBattleStart(AbstractDungeon.player.drawPile);
        }
        for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
            if (c instanceof Daedalus) c.applyPowers();
            if (c instanceof Eurora) ((Eurora) c).atBattleStart();
            if (c instanceof Mapping) ((Mapping) c).atBattleStart(AbstractDungeon.player.discardPile);
        }
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c instanceof Daedalus) c.applyPowers();
            if (c instanceof Eurora) ((Eurora) c).atBattleStart();
            if (c instanceof Mapping) ((Mapping) c).atBattleStart(AbstractDungeon.player.hand);
        }
    }

    @Override
    public void receivePostDraw(AbstractCard arg0) {
        DrawPileMonitor.CardIsDrawn.add(arg0);
    }

    @Override
    public void receivePowersModified() {
    }

    @Override
    public int receiveOnPlayerDamaged(int var1, DamageInfo var2) {
        return var1;
    }

    @Override
    public void receivePostExhaust(AbstractCard c) {
    }

    @Override
    public void receivePostPowerApplySubscriber(AbstractPower pow, AbstractCreature target, AbstractCreature owner) {
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        EndTurnButtonPatch.ended = false;
        TurnCounter++;

        AbstractPlayer p = AbstractDungeon.player;
        Stream.of(p.hand.group,
                p.discardPile.group,
                p.drawPile.group,
                p.exhaustPile.group).flatMap(Collection::stream).forEach(c -> {
                    if (c instanceof SpottingRound)
                        ((SpottingRound) c).init();
                }
        );
    }

    @Override
    public void receivePostRender(SpriteBatch spriteBatch) {
    }

    @Override
    public void receivePostUpdate() {
        RightClickPatch.update();

        if (ConfigHelper.llmEventEnabled && com.megacrit.cardcrawl.dungeons.AbstractDungeon.player != null) {
            int floor = com.megacrit.cardcrawl.dungeons.AbstractDungeon.floorNum;
            if (floor != lastSeenFloorForLLMPrefetch) {
                lastSeenFloorForLLMPrefetch = floor;
                
                // Only prefetch if N+2 floor exists and might be an event
                // Check if we can access the dungeon map
                if (com.megacrit.cardcrawl.dungeons.AbstractDungeon.map != null &&
                        !AbstractDungeon.map.isEmpty()) {
                    
                    int targetFloor = floor + 2;
                    // Only request if target floor is within reasonable range and there is an EVENT node reachable in +2 steps
                    if (targetFloor > 0) {
                        boolean hasEvent = anyNodeTwoAheadIsEvent();
                        logger.info("[LLM] Floor changed to {}. Check N+2={}: hasEvent={}", floor, targetFloor, hasEvent);
                        if (hasEvent) {
                            LLMEventService.requestForFloorIfNeeded(targetFloor);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if any map node reachable in exactly two steps from the current node is an EventRoom.
     * This considers both flat and nested AbstractDungeon.map representations.
     */
    private boolean anyNodeTwoAheadIsEvent() {
        try {
            com.megacrit.cardcrawl.map.MapRoomNode curr = com.megacrit.cardcrawl.dungeons.AbstractDungeon.currMapNode;
            if (curr == null) return false;
            
            ArrayList<ArrayList<com.megacrit.cardcrawl.map.MapRoomNode>> map = com.megacrit.cardcrawl.dungeons.AbstractDungeon.map;
            if (map == null) return false;

            // Step 1: Check nodes reachable in 1 step
            for (com.megacrit.cardcrawl.map.MapEdge edge1 : curr.getEdges()) {
                int x1 = edge1.dstX;
                int y1 = edge1.dstY;
                
                // Safety checks for map bounds
                if (y1 < 0 || y1 >= map.size()) continue;
                ArrayList<com.megacrit.cardcrawl.map.MapRoomNode> row1 = map.get(y1);
                if (x1 < 0 || x1 >= row1.size()) continue;
                
                com.megacrit.cardcrawl.map.MapRoomNode node1 = row1.get(x1);
                if (node1 == null) continue;

                // Step 2: Check nodes reachable in 2 steps (from node1)
                for (com.megacrit.cardcrawl.map.MapEdge edge2 : node1.getEdges()) {
                    int x2 = edge2.dstX;
                    int y2 = edge2.dstY;
                    
                    if (y2 < 0 || y2 >= map.size()) continue;
                    ArrayList<com.megacrit.cardcrawl.map.MapRoomNode> row2 = map.get(y2);
                    if (x2 < 0 || x2 >= row2.size()) continue;
                    
                    com.megacrit.cardcrawl.map.MapRoomNode node2 = row2.get(x2);
                    if (node2 != null && node2.room instanceof com.megacrit.cardcrawl.rooms.EventRoom) {
                        // Found a reachable event room 2 steps away
                        return true;
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            // Be conservative on failure: do not prefetch
            logger.warn("[LLM] Error checking reachable N+2 event nodes: {}", e.toString());
            return false;
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        if (AbstractDungeon.player.hasRelic(ATRelic.ID)) {
            AbstractDungeon.effectList.add(new ShowATEffect(true));
        }
    }

    class Keywords {
        Keyword[] keywords;
    }
}

