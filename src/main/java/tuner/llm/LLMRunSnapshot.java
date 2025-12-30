package tuner.llm;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.MetricData;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LLMRunSnapshot {
    public long timestampMs;

    public String playerName;
    public String characterClass;

    public int floor;
    public int act;
    public int ascension;

    public long seed;

    public int hp;
    public int maxHp;
    public int gold;

    public int deckSize;
    public int relicCount;

    // Detailed info for better context
    public ArrayList<String> relics = new ArrayList<>();
    public ArrayList<CardInfo> deck = new ArrayList<>();
    public ArrayList<String> potions = new ArrayList<>();
    
    // Combat history from this run
    public ArrayList<CombatInfo> recentCombats = new ArrayList<>();
    
    // Card type counts for deck analysis
    public int attackCount;
    public int skillCount;
    public int powerCount;
    public int curseCount;
    public int statusCount;

    public String language;

    // Nested class for card info
    public static class CardInfo {
        public String id;
        public String name;
        public String type;
        public String rarity;
        public int cost;
        public int upgrades;

        public CardInfo(AbstractCard card) {
            this.id = card.cardID;
            this.name = card.name;
            this.type = card.type == null ? "UNKNOWN" : card.type.name();
            this.rarity = card.rarity == null ? "UNKNOWN" : card.rarity.name();
            this.cost = card.cost;
            this.upgrades = card.timesUpgraded;
        }
    }

    // Nested class for combat history
    public static class CombatInfo {
        public String encounterName;
        public int damageDealt;
        public int damageTaken;
        public int turnsUsed;
        public int cardsPlayed;
        public boolean wasElite;
        public boolean wasBoss;
        public int floor;
    }

    public static LLMRunSnapshot capture() {
        LLMRunSnapshot s = new LLMRunSnapshot();
        s.timestampMs = System.currentTimeMillis();

        s.playerName = CardCrawlGame.playerName;
        s.language = Settings.language == null ? "UNKNOWN" : Settings.language.name();

        if (AbstractDungeon.player != null) {
            s.characterClass = AbstractDungeon.player.chosenClass == null ? "UNKNOWN" : AbstractDungeon.player.chosenClass.name();
            s.hp = AbstractDungeon.player.currentHealth;
            s.maxHp = AbstractDungeon.player.maxHealth;
            s.gold = AbstractDungeon.player.gold;

            // Deck info
            if (AbstractDungeon.player.masterDeck != null) {
                s.deckSize = AbstractDungeon.player.masterDeck.size();
                for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                    s.deck.add(new CardInfo(card));
                    
                    // Count by type
                    if (card.type == AbstractCard.CardType.ATTACK) s.attackCount++;
                    else if (card.type == AbstractCard.CardType.SKILL) s.skillCount++;
                    else if (card.type == AbstractCard.CardType.POWER) s.powerCount++;
                    else if (card.type == AbstractCard.CardType.CURSE) s.curseCount++;
                    else if (card.type == AbstractCard.CardType.STATUS) s.statusCount++;
                }
            }

            // Relic info
            s.relicCount = AbstractDungeon.player.relics == null ? 0 : AbstractDungeon.player.relics.size();
            if (AbstractDungeon.player.relics != null) {
                AbstractDungeon.player.relics.forEach(r -> s.relics.add(r.relicId));
            }

            // Potion info
            if (AbstractDungeon.player.potions != null) {
                for (AbstractPotion p : AbstractDungeon.player.potions) {
                    if (p != null && !(p instanceof PotionSlot)) {
                        s.potions.add(p.ID);
                    }
                }
            }
        }

        s.floor = AbstractDungeon.floorNum;
        s.act = AbstractDungeon.actNum;
        s.ascension = AbstractDungeon.ascensionLevel;
        s.seed = Settings.seed == null ? 0L : Settings.seed;

        // Capture combat history from metrics
        try {
            if (CardCrawlGame.metricData != null && CardCrawlGame.metricData.damage_taken != null) {
                int combatCount = Math.min(5, CardCrawlGame.metricData.damage_taken.size());
                for (int i = CardCrawlGame.metricData.damage_taken.size() - combatCount; i < CardCrawlGame.metricData.damage_taken.size(); i++) {
                    if (i < 0) continue;
                    HashMap<String, Object> combat = CardCrawlGame.metricData.damage_taken.get(i);
                    if (combat == null) continue;
                    
                    CombatInfo info = new CombatInfo();
                    info.encounterName = getStringOrDefault(combat, "enemies", "Unknown");
                    info.damageTaken = getIntOrDefault(combat, "damage", 0);
                    info.turnsUsed = getIntOrDefault(combat, "turns", 0);
                    info.floor = getIntOrDefault(combat, "floor", 0);
                    
                    s.recentCombats.add(info);
                }
            }
        } catch (Exception ignored) {
            // Metrics might not be available
        }

        return s;
    }

    private static String getStringOrDefault(HashMap<String, Object> map, String key, String defaultValue) {
        Object val = map.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    private static int getIntOrDefault(HashMap<String, Object> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return defaultValue;
    }
}
