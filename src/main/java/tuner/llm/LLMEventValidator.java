package tuner.llm;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LLMEventValidator {
    public static final int MAX_OPTIONS = 4;
    public static final int MAX_EFFECTS_PER_OPTION = 3;

    public static final int MAX_GOLD = 200;
    public static final int MAX_HEAL = 30;

    private static final int MAX_NAME_LEN = 80;
    private static final int MAX_DESC_LEN = 800;
    private static final int MAX_OPTION_TEXT_LEN = 140;
    private static final int MAX_PROMPT_LEN = 200;
    private static final int MAX_TOKEN_LEN = 64;
    private static final int MAX_REVEAL_TEXT_LEN = 800;

    private LLMEventValidator() {
    }

    public static LLMEventSpec normalize(LLMEventSpec in) {
        if (in == null) return null;

        LLMEventSpec out = new LLMEventSpec();
        out.name = safeText(in.name, "LLM Event", MAX_NAME_LEN);

        if (in.descriptions != null) {
            for (String d : in.descriptions) {
                String dd = safeText(d, null, MAX_DESC_LEN);
                if (dd != null) out.descriptions.add(dd);
            }
        }
        if (out.descriptions.isEmpty()) {
            out.descriptions.add("...");
        }

        if (in.options != null) {
            for (LLMEventOption opt : in.options) {
                if (out.options.size() >= MAX_OPTIONS) break;
                LLMEventOption o = normalizeOption(opt);
                if (o != null) out.options.add(o);
            }
        }

        if (out.options.isEmpty()) {
            LLMEventOption leave = new LLMEventOption();
            // Use localized leave text if available
            try {
                com.megacrit.cardcrawl.localization.EventStrings s = com.megacrit.cardcrawl.core.CardCrawlGame.languagePack.getEventString("tuner:LLMGeneratedEvent");
                if (s != null && s.OPTIONS != null && s.OPTIONS.length > 0) {
                    leave.text = s.OPTIONS[0];
                } else {
                    leave.text = "[Leave]";
                }
            } catch (Exception ex) {
                leave.text = "[Leave]";
            }
            out.options.add(leave);
        }

        return out;
    }

    private static LLMEventOption normalizeOption(LLMEventOption opt) {
        if (opt == null) return null;
        String text = safeText(opt.text, "[Continue]", MAX_OPTION_TEXT_LEN);

        LLMEventOption out = new LLMEventOption();
        out.text = text;

        // Visible (bait) effects
        out.effects = normalizeAndMergeEffects(opt.effects, false);

        // Hidden (trap) effects + reveal text
        out.hiddenEffects = normalizeAndMergeEffects(opt.hiddenEffects, true);
        out.revealText = safeText(opt.revealText, null, MAX_REVEAL_TEXT_LEN);

        // For small rewards, allow only mild hidden penalties (conservative caps).
        if (isSmallOrNoReward(out.effects)) {
            ArrayList<LLMEffect> filtered = new ArrayList<>();
            int curseCount = 0;
            for (LLMEffect he : out.hiddenEffects) {
                if (he == null || he.type == null) continue;
                boolean ok = false;
                switch (he.type) {
                    case LOSE_GOLD:
                        if (he.amount != null && he.amount <= 15) ok = true;
                        break;
                    case LOSE_HP:
                        if (he.amount != null && he.amount <= 5) ok = true;
                        break;
                    case LOSE_MAX_HP:
                        if (he.amount != null && he.amount <= 1) ok = true;
                        break;
                    case GAIN_CURSE:
                        if (curseCount == 0) { ok = true; curseCount++; }
                        break;
                    default:
                        ok = false;
                }
                if (ok) filtered.add(he);
            }
            out.hiddenEffects = filtered;
            if (out.hiddenEffects.isEmpty()) {
                // If the hidden list was removed, clear revealText to avoid confusing reveals
                out.revealText = null;
            }
        }

        return out;
    }

    private static ArrayList<LLMEffect> normalizeAndMergeEffects(ArrayList<LLMEffect> effects, boolean hidden) {
        ArrayList<LLMEffect> out = new ArrayList<>();
        if (effects == null || effects.isEmpty()) return out;

        // Merge duplicates by type; preserve first-seen order.
        Map<LLMEffectType, LLMEffect> merged = new EnumMap<>(LLMEffectType.class);
        ArrayList<LLMEffectType> order = new ArrayList<>();

        for (LLMEffect raw : effects) {
            LLMEffect eff = normalizeEffect(raw, hidden);
            if (eff == null || eff.type == null) continue; // unknown/invalid dropped

            LLMEffect existing = merged.get(eff.type);
            if (existing == null) {
                merged.put(eff.type, eff);
                order.add(eff.type);
                continue;
            }

            switch (eff.type) {
                case GAIN_GOLD:
                case HEAL:
                case GAIN_MAX_HP:
                case LOSE_GOLD:
                case LOSE_HP:
                case LOSE_MAX_HP:
                    existing.amount = (existing.amount == null ? 0 : existing.amount) + (eff.amount == null ? 0 : eff.amount);
                    break;
                case CARD_REWARD:
                    if ((existing.prompt == null || existing.prompt.isEmpty()) && eff.prompt != null && !eff.prompt.isEmpty()) {
                        existing.prompt = eff.prompt;
                    }
                    break;
                case GIVE_RELIC:
                case START_COMBAT:
                case START_HARD_COMBAT:
                case GAIN_CURSE:
                    // Keep first; ignore duplicates.
                    break;
            }
        }

        for (LLMEffectType t : order) {
            if (out.size() >= MAX_EFFECTS_PER_OPTION) break;
            LLMEffect eff = merged.get(t);
            if (eff == null) continue;

            if (t == LLMEffectType.GAIN_GOLD) {
                int v = clamp(eff.amount == null ? 0 : eff.amount, 0, MAX_GOLD);
                if (v <= 0) continue;
                eff.amount = v;
            } else if (t == LLMEffectType.HEAL) {
                int v = clamp(eff.amount == null ? 0 : eff.amount, 0, MAX_HEAL);
                if (v <= 0) continue;
                eff.amount = v;
            } else if (t == LLMEffectType.GAIN_MAX_HP) {
                int v = clamp(eff.amount == null ? 0 : eff.amount, 1, 15);
                eff.amount = v;
            } else if (t == LLMEffectType.LOSE_GOLD) {
                int v = clamp(eff.amount == null ? 0 : eff.amount, 1, 200);
                eff.amount = v;
            } else if (t == LLMEffectType.LOSE_HP) {
                int v = clamp(eff.amount == null ? 0 : eff.amount, 1, 50);
                eff.amount = v;
            } else if (t == LLMEffectType.LOSE_MAX_HP) {
                int v = clamp(eff.amount == null ? 0 : eff.amount, 1, 15);
                eff.amount = v;
            }

            out.add(eff);
        }

        return out;
    }

    private static LLMEffect normalizeEffect(LLMEffect e, boolean hidden) {
        // Unknown effect strings deserialize to null enum -> drop.
        if (e == null || e.type == null) return null;

        // Enforce visibility separation strictly:
        // - visible list must NOT contain negative/trap types
        // - hidden list must NOT contain positive/reward types
        if (!hidden && isHiddenOnlyType(e.type)) return null;
        if (hidden && isVisibleOnlyType(e.type)) return null;

        LLMEffect out = new LLMEffect();
        out.type = e.type;
        out.prompt = safeText(e.prompt, null, MAX_PROMPT_LEN);
        out.rarity = safeToken(e.rarity);
        out.pool = safeToken(e.pool);
        out.encounterKey = safeToken(e.encounterKey);

        switch (e.type) {
            case GAIN_GOLD:
                out.amount = clamp(e.amount == null ? 0 : e.amount, 0, MAX_GOLD);
                if (out.amount <= 0) return null;
                break;
            case HEAL:
                out.amount = clamp(e.amount == null ? 0 : e.amount, 0, MAX_HEAL);
                if (out.amount <= 0) return null;
                break;
            case GAIN_MAX_HP:
                out.amount = clamp(e.amount == null ? 0 : Math.abs(e.amount), 1, 15);
                break;
            case CARD_REWARD:
                break;
            case GIVE_RELIC:
                // Optional, but if provided must be a known tier
                if (out.rarity != null) {
                    String r = out.rarity.trim().toUpperCase();
                    if (!"COMMON".equals(r) && !"UNCOMMON".equals(r) && !"RARE".equals(r)) {
                        out.rarity = null;
                    }
                }
                break;
            case START_COMBAT:
                // Validate encounter key is provided
                if (out.encounterKey == null || out.encounterKey.trim().isEmpty()) {
                    return null;
                }
                break;
            case LOSE_GOLD:
                out.amount = clamp(e.amount == null ? 0 : Math.abs(e.amount), 1, MAX_GOLD);
                break;
            case LOSE_HP:
                out.amount = clamp(e.amount == null ? 0 : Math.abs(e.amount), 1, 50);
                break;
            case GAIN_CURSE:
                break;
            case LOSE_MAX_HP:
                out.amount = clamp(e.amount == null ? 0 : Math.abs(e.amount), 1, 15);
                break;
            case START_HARD_COMBAT:
                // No params required; ignore encounterKey if present
                out.encounterKey = null;
                out.amount = null;
                out.prompt = null;
                out.rarity = null;
                out.pool = null;
                break;
        }

        return out;
    }

    private static boolean isHiddenOnlyType(LLMEffectType type) {
        if (type == null) return false;
        switch (type) {
            case LOSE_GOLD:
            case LOSE_HP:
            case GAIN_CURSE:
            case LOSE_MAX_HP:
            case START_HARD_COMBAT:
                return true;
            default:
                return false;
        }
    }

    private static boolean isVisibleOnlyType(LLMEffectType type) {
        if (type == null) return false;
        switch (type) {
            case GAIN_GOLD:
            case HEAL:
            case CARD_REWARD:
            case GIVE_RELIC:
            case START_COMBAT:
            case GAIN_MAX_HP:
                return true;
            default:
                return false;
        }
    }

    private static boolean isSmallOrNoReward(ArrayList<LLMEffect> visibleEffects) {
        if (visibleEffects == null || visibleEffects.isEmpty()) return true;

        int gold = 0;
        int heal = 0;
        int maxHp = 0;
        boolean hasRelic = false;
        boolean hasCard = false;
        boolean hasCombat = false;

        for (LLMEffect e : visibleEffects) {
            if (e == null || e.type == null) continue;
            switch (e.type) {
                case GAIN_GOLD:
                    gold += e.amount == null ? 0 : e.amount;
                    break;
                case HEAL:
                    heal += e.amount == null ? 0 : e.amount;
                    break;
                case GAIN_MAX_HP:
                    maxHp += e.amount == null ? 0 : e.amount;
                    break;
                case GIVE_RELIC:
                    hasRelic = true;
                    break;
                case CARD_REWARD:
                    hasCard = true;
                    break;
                case START_COMBAT:
                    hasCombat = true;
                    break;
            }
        }

        // Any relic/card/combat is NOT considered small.
        if (hasRelic || hasCard || hasCombat) return false;

        // Small reward thresholds:
        // - gold <= 30
        // - heal <= 10
        // - maxHp <= 3
        return gold <= 30 && heal <= 10 && maxHp <= 3;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String safeText(String s, String fallback) {
        return safeText(s, fallback, 200);
    }

    private static String safeText(String s, String fallback, int maxLen) {
        if (s == null) return fallback;
        String t = sanitizeText(s);
        t = t.trim();
        if (t.isEmpty()) return fallback;
        if (t.length() > maxLen) return t.substring(0, maxLen);
        return t;
    }

    private static String safeToken(String s) {
        return safeText(s, null, MAX_TOKEN_LEN);
    }

    // Remove illegal control characters while keeping newlines/tabs.
    // Also convert LLM hex color markers like "[#66ff66]" into short color tokens the game understands ("#g", "#r", "#b", "#y").
    private static String sanitizeText(String s) {
        if (s == null) return null;

        // Convert hex color markers to short tokens first.
        s = replaceHexColorTags(s);

        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\n' || ch == '\r' || ch == '\t') {
                sb.append(ch);
                continue;
            }
            // Drop ASCII control chars and DEL.
            if (ch < 32 || ch == 127) {
                continue;
            }
            sb.append(ch);
        }

        // Normalize inline color tokens (e.g., use ASCII '#', lowercase letters, remove zero-width chars)
        String out = normalizeColorTokens(sb.toString());
        return out;
    }

    // Normalize inline color tokens for safe display and parsing
    public static String normalizeColorTokens(String s) {
        if (s == null) return null;
        // Replace fullwidth hash with ASCII hash
        s = s.replace('＃', '#');
        // Remove common invisible characters that can break token parsing
        s = s.replace("\u200B", "").replace("\uFEFF", "");

        // Lowercase the token letter after '#', only for single letter tokens
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("#([A-Za-z])");
        java.util.regex.Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String ch = m.group(1).toLowerCase();
            m.appendReplacement(sb, "#" + ch);
        }
        m.appendTail(sb);
        return sb.toString();
    }
    private static String replaceHexColorTags(String s) {
        Pattern p = Pattern.compile("\\[#([0-9a-fA-F]{6})\\]");
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String hex = m.group(1);
            String token = mapHexToShortColor(hex);
            // appendReplacement treats backslashes and dollar signs specially; token contains only simple chars (# and letter)
            m.appendReplacement(sb, token);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String mapHexToShortColor(String hex) {
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            // Simple heuristic: dominant channel -> token
            if (r > g && r > b) return "#r"; // red
            if (g > r && g > b) return "#g"; // green
            if (b > r && b > g) return "#b"; // blue
            // If red+green dominate -> yellow
            if (r + g > b + 50) return "#y";
            // default to green
            return "#g";
        } catch (Exception ex) {
            return "#g";
        }
    }
}
