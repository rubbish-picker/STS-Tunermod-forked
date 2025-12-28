package tuner.misc.shijian;

import com.codedisaster.steamworks.*;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import tuner.helpers.ConfigHelper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import static com.megacrit.cardcrawl.metrics.Metrics.timestampFormatter;

public class Shijian {
    public static MySteamUGCCallback UGCCallback;
    public static MySteamUserCallback userCallback;
    public static SteamUGC steamUgc;
    public static SteamUser steamUser;

    public static String steamUserName = "NULL";

    public static void shijian() {
        if (SteamAPI.isSteamRunning()) {
            if (userCallback == null) {
                userCallback = new MySteamUserCallback();
                steamUser = new SteamUser(userCallback);
            }
            SteamID steamID = steamUser.getSteamID();

            steamUserName = String.valueOf((SteamNativeHandle.getNativeHandle(steamID)));

            if (UGCCallback == null) {
                UGCCallback = new MySteamUGCCallback();
                steamUgc = new SteamUGC(UGCCallback);
            }
            steamUgc.getUserItemVote(new SteamPublishedFileID(3357135443L));
        }
    }

    public static void giveMe(int i, int status) {
//        SimpleHttpClient.sendMessage("GIVE ME", aDouble -> {
//        });
    }

    public static String gatherAllData(boolean death, boolean trueVictor, MonsterGroup monsters, int voteStatus) {
        HashMap<Object, Object> data = new HashMap<>();
        data.put("play_name", CardCrawlGame.playerName);
        data.put("difficult_mod", ConfigHelper.difficultMod);
        data.put("play_id", UUID.randomUUID().toString());
        data.put("build_version", CardCrawlGame.TRUE_VERSION_NUM);
        data.put("seed_played", Settings.seed.toString());
        data.put("chose_seed", Settings.seedSet);
        data.put("seed_source_timestamp", Settings.seedSourceTimestamp);
        data.put("is_daily", Settings.isDailyRun);
        data.put("special_seed", Settings.specialSeed);
        if (ModHelper.enabledMods.size() > 0) {
            data.put("daily_mods", ModHelper.getEnabledModIDs());
        }

        data.put("is_trial", Settings.isTrial);
        data.put("is_endless", Settings.isEndless);
        if (death) {
            AbstractPlayer player = AbstractDungeon.player;
            CardCrawlGame.metricData.current_hp_per_floor.add(player.currentHealth);
            CardCrawlGame.metricData.max_hp_per_floor.add(player.maxHealth);
            CardCrawlGame.metricData.gold_per_floor.add(player.gold);
        }

        data.put("is_ascension_mode", AbstractDungeon.isAscensionMode);
        data.put("ascension_level", AbstractDungeon.ascensionLevel);
        data.put("neow_bonus", CardCrawlGame.metricData.neowBonus);
        data.put("neow_cost", CardCrawlGame.metricData.neowCost);
        data.put("is_beta", Settings.isBeta);
        data.put("is_prod", Settings.isDemo);
        data.put("victory", !death);
        data.put("floor_reached", AbstractDungeon.floorNum);
        if (trueVictor) {
            data.put("score", VictoryScreen.calcScore(!death));
        } else {
            data.put("score", DeathScreen.calcScore(!death));
        }

        data.put("timestamp", System.currentTimeMillis() / 1000L);
        data.put("local_time", timestampFormatter.format(Calendar.getInstance().getTime()));
        data.put("playtime", (long) CardCrawlGame.playtime);
        data.put("player_experience", Settings.totalPlayTime);
        data.put("master_deck", AbstractDungeon.player.masterDeck.getCardIdsForMetrics());
        data.put("relics", AbstractDungeon.player.getRelicNames());
        data.put("gold", AbstractDungeon.player.gold);
        data.put("campfire_rested", CardCrawlGame.metricData.campfire_rested);
        data.put("campfire_upgraded", CardCrawlGame.metricData.campfire_upgraded);
        data.put("purchased_purges", CardCrawlGame.metricData.purchased_purges);
        data.put("potions_floor_spawned", CardCrawlGame.metricData.potions_floor_spawned);
        data.put("potions_floor_usage", CardCrawlGame.metricData.potions_floor_usage);
        data.put("current_hp_per_floor", CardCrawlGame.metricData.current_hp_per_floor);
        data.put("max_hp_per_floor", CardCrawlGame.metricData.max_hp_per_floor);
        data.put("gold_per_floor", CardCrawlGame.metricData.gold_per_floor);
        data.put("path_per_floor", CardCrawlGame.metricData.path_per_floor);
        data.put("path_taken", CardCrawlGame.metricData.path_taken);
        data.put("items_purchased", CardCrawlGame.metricData.items_purchased);
        data.put("item_purchase_floors", CardCrawlGame.metricData.item_purchase_floors);
        data.put("items_purged", CardCrawlGame.metricData.items_purged);
        data.put("items_purged_floors", CardCrawlGame.metricData.items_purged_floors);
        data.put("character_chosen", AbstractDungeon.player.chosenClass.name());
        data.put("card_choices", CardCrawlGame.metricData.card_choices);
        data.put("event_choices", CardCrawlGame.metricData.event_choices);
        data.put("boss_relics", CardCrawlGame.metricData.boss_relics);
        data.put("damage_taken", CardCrawlGame.metricData.damage_taken);
        data.put("potions_obtained", CardCrawlGame.metricData.potions_obtained);
        data.put("relics_obtained", CardCrawlGame.metricData.relics_obtained);
        data.put("campfire_choices", CardCrawlGame.metricData.campfire_choices);
        data.put("circlet_count", AbstractDungeon.player.getCircletCount());
        Prefs pref = AbstractDungeon.player.getPrefs();
        int numVictory = pref.getInteger("WIN_COUNT", 0);
        int numDeath = pref.getInteger("LOSE_COUNT", 0);
        if (numVictory <= 0) {
            data.put("win_rate", 0.0F);
        } else {
            data.put("win_rate", numVictory / (numDeath + numVictory));
        }

        if (death && monsters != null) {
            data.put("killed_by", AbstractDungeon.lastCombatMetricKey);
        } else {
            data.put("killed_by", (Object) null);
        }

        if(voteStatus == 0)
            data.put("vote_status", "UP");
        else if(voteStatus == 1)
            data.put("vote_status", "DOWN");
        else if(voteStatus == 2)
            data.put("vote_status", "NULL");

        Gson gson = new Gson();
        return gson.toJson(data);
    }
}
