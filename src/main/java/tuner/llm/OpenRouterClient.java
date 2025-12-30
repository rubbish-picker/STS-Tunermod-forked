package tuner.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tuner.helpers.ConfigHelper;
import tuner.misc.SimpleHttpClient;

import java.io.IOException;

/**
 * Client for calling OpenRouter API to generate event specifications.
 */
public final class OpenRouterClient {
    private static final Logger logger = LogManager.getLogger(OpenRouterClient.class);
    private static final Gson GSON = new Gson();

    private OpenRouterClient() {
    }

    /**
     * Calls OpenRouter API with the given snapshot and returns a parsed LLMEventSpec.
     */
    public static LLMEventSpec generateEventSpec(LLMRunSnapshot snapshot, int targetFloor) throws IOException {
        String apiKey = ConfigHelper.llmApiKey;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IOException("OpenRouter API key not configured");
        }

        String endpoint = ConfigHelper.llmEndpoint;
        String model = ConfigHelper.llmModel;

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(snapshot, targetFloor);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);

        JsonArray messages = new JsonArray();

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);
        messages.add(systemMsg);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userPrompt);
        messages.add(userMsg);

        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.8);
        requestBody.addProperty("max_tokens", 1500);

        String body = GSON.toJson(requestBody);

        SimpleHttpClient.HttpRequest request = new SimpleHttpClient.HttpRequest.Builder()
                .url(endpoint)
                .method("POST")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey.trim())
                .addHeader("HTTP-Referer", "https://github.com/tuner-mod")
                .addHeader("X-Title", "STS Tuner Mod")
                .body(body)
                .build();

        SimpleHttpClient.HttpResponse resp = SimpleHttpClient.sendRequest(request);

        if (resp.getStatusCode() < 200 || resp.getStatusCode() >= 300) {
            String preview = resp.getBody();
            if (preview != null && preview.length() > 500) {
                preview = preview.substring(0, 500);
            }
            throw new IOException("OpenRouter API error: " + resp.getStatusCode() + " - " + preview);
        }

        String responseBody = resp.getBody();
        if (responseBody == null || responseBody.isEmpty()) {
            throw new IOException("Empty response from OpenRouter");
        }

        return parseResponse(responseBody);
    }

    private static String buildSystemPrompt() {
        return "Write a Slay the Spire '?' room event. Output MUST be ONLY a JSON object (no markdown, no extra text).\n" +
            "All player-facing text MUST be in the language specified by the user prompt.\n" +
            "The event must feel reactive to THIS run (mention at least 2 provided details).\n" +
            "Greed trap guidance: Prefer variety rather than a fixed template. Small or modest rewards SHOULD NOT have severe hidden penalties — they may include mild, subtle costs (e.g., lose a small amount of gold, lose a few HP, or a single curse) but any such hidden penalty must be minor and explained in the revealText. Greedy big rewards should often include a meaningful twist (hiddenEffects + revealText), but do NOT always make the big reward a gold+relic combo.\n\n" +
            "STRICT SCHEMA:\n" +
            "{\n" +
            "  \"name\": string,\n" +
            "  \"descriptions\": [string, string],\n" +
            "  \"options\": [\n" +
            "    {\n" +
            "      \"text\": string,\n" +
            "      \"effects\": [Effect],\n" +
            "      \"hiddenEffects\": [Effect],\n" +
            "      \"revealText\": string|null\n" +
            "    }\n" +
            "  ]\n" +
            "}\n\n" +
            "Effects forms:\n" +
            "- {\"type\":\"GAIN_GOLD\",\"amount\":1-200}\n" +
            "- {\"type\":\"HEAL\",\"amount\":1-30}\n" +
            "- {\"type\":\"GAIN_MAX_HP\",\"amount\":1-15}\n" +
            "- {\"type\":\"CARD_REWARD\",\"prompt\":string}\n" +
            "- {\"type\":\"GIVE_RELIC\",\"rarity\":\"COMMON\"|\"UNCOMMON\"|\"RARE\"}\n" +
            "- {\"type\":\"START_COMBAT\",\"encounterKey\":string}\n" +
            "hiddenEffects forms:\n" +
            "- {\"type\":\"LOSE_GOLD\",\"amount\":1-200}\n" +
            "- {\"type\":\"LOSE_HP\",\"amount\":1-50}\n" +
            "- {\"type\":\"GAIN_CURSE\"}\n" +
            "- {\"type\":\"LOSE_MAX_HP\",\"amount\":1-15}\n" +
            "- {\"type\":\"START_HARD_COMBAT\"}\n\n" +
            "Constraints:\n" +
            "- Max 3 effects in effects, max 3 in hiddenEffects (the amount should be varied)\n" +
            "- If you create a leave option, then it should have effects=[] and hiddenEffects=[]\n" +
            "- Titles must be varied; avoid repeating the same title\n" +

            "Color usage rule: Use Slay the Spire inline color tokens to color text. " +
            "Specifically, prefer the short tokens `#g` (green), `#r` (red), `#b` (blue), " +
            "and `#y` (yellow) placed immediately before the word or phrase to color (no closing tag). " +
            "Do NOT use hex color codes like `[#66ff66]`, HTML tags, or bracketed color markers. " +
            "Example: \"You feel the #ggolden flame#y warming you\" " +
            "or \"Take #g50 Gold\" (use tokens inline, no brackets).\n\n" +

            "Placeholders for dynamic run state: When referring to mutable values that may change between prefetch time and room entry, DO NOT insert concrete numbers. Instead use placeholders so the game will substitute current values at room entry. Supported placeholders: {HP}, {MAX_HP}, {GOLD}, {DECK_SIZE}, {CURSE_COUNT}, {RELICS}, {FLOOR}. Example: \"Restore {HP} HP\" or \"Gain {GOLD} gold\". The LLM must NOT resolve these placeholders into numbers. Use placeholders only in *player-facing text* (names, descriptions, revealText). Option content and Effect parameters (e.g. the numeric fields in effects such as \"amount\") must be concrete numbers and not placeholders.";           
    }

    private static String buildUserPrompt(LLMRunSnapshot snapshot, int targetFloor) {
        StringBuilder sb = new StringBuilder();
        sb.append("LANGUAGE: ").append(snapshot.language).append(" (use this language for ALL text)\n");
        sb.append("Character: ").append(snapshot.characterClass).append(" | Act ").append(snapshot.act).append(" | Floor ").append(targetFloor).append("\n");
        sb.append("HP: ").append(snapshot.hp).append("/").append(snapshot.maxHp).append(" | Gold: ").append(snapshot.gold).append(" | Ascension: ").append(snapshot.ascension).append("\n");
        sb.append("Deck: size=").append(snapshot.deckSize)
                .append(", A=").append(snapshot.attackCount)
                .append(", S=").append(snapshot.skillCount)
                .append(", P=").append(snapshot.powerCount)
                .append(", Curses=").append(snapshot.curseCount)
                .append(" | Style=").append(detectArchetype(snapshot)).append("\n");

        // if (snapshot.relics != null && !snapshot.relics.isEmpty()) {
        //     sb.append("Relics (up to 6): ");
        //     for (int i = 0; i < Math.min(6, snapshot.relics.size()); i++) {
        //         if (i > 0) sb.append(", ");
        //         sb.append(snapshot.relics.get(i));
        //     }
        //     sb.append("\n");
        // }

        if (snapshot.recentCombats != null && !snapshot.recentCombats.isEmpty()) {
            sb.append("Recent combats (up to 2): ");
            int n = 0;
            for (LLMRunSnapshot.CombatInfo c : snapshot.recentCombats) {
                if (n >= 2) break;
                if (n > 0) sb.append(" | ");
                sb.append(c.encounterName).append(" dmg=").append(c.damageTaken);
                n++;
            }
            sb.append("\n");
        }

        sb.append("\nRequirements:\n");
sb.append("- 2-5 options; Leave is optional (may be absent) and option order should vary — do NOT always output the same three-option template.\n");
        sb.append("- Small rewards (<=30 gold, <=10 heal, <=3 maxHP) should not have severe traps; mild hidden penalties are allowed (e.g., lose <=15 gold, lose <=5 HP, lose <=1 max HP, or a single curse) but keep them minor and explain them in revealText if present.\n");
        sb.append("- Greedy big rewards should often include a twist (hiddenEffects + revealText) but avoid always using gold+relic as the only big reward — use heal, cards, max HP, unique choices, or encounter choices too.\n");
        sb.append("- Text must relate to THIS run (mention at least 2 details above)\n");
        sb.append("- When referring to values that may change between prefetch and the room entry, use placeholders: {HP}, {MAX_HP}, {GOLD}, {DECK_SIZE}, {CURSE_COUNT}, {RELICS}, {FLOOR}. Do NOT put concrete numbers for these values; they will be substituted by the game when the room is opened.\n\n");
        sb.append("- Use Slay the Spire inline color tokens for emphasis: #g (green), #r (red), #b (blue), #y (yellow). Place token immediately before the word/phrase to color; do NOT output hex codes like [#66ff66] or HTML tags. Example: \"#gGain 50 Gold\" or \"#rLose 10 HP\".\n\n");
        sb.append("description的文风说明：允许适当包含：对玩家进行幽默、讽刺挖苦或者玩一些杀戮尖塔的梗。") ;
        sb.append("Return ONLY the JSON object.");

        return sb.toString();
    }

    private static String detectArchetype(LLMRunSnapshot snapshot) {
        if (snapshot.deckSize == 0) return "Unknown";
        
        float attackRatio = (float) snapshot.attackCount / snapshot.deckSize;
        float powerRatio = (float) snapshot.powerCount / snapshot.deckSize;
        float skillRatio = (float) snapshot.skillCount / snapshot.deckSize;
        
        if (attackRatio > 0.6) return "Aggressive/Strike-heavy";
        if (powerRatio > 0.2) return "Power-scaling/Setup";
        if (skillRatio > 0.5) return "Defensive/Control";
        if (snapshot.deckSize <= 15) return "Lean/Thin deck";
        if (snapshot.deckSize >= 30) return "Large/Varied deck";
        return "Balanced";
    }

    private static LLMEventSpec parseResponse(String responseBody) throws IOException {
        try {
            logger.info("[LLM] Parsing response, length={}", responseBody.length());
            
            JsonParser parser = new JsonParser();
            JsonObject root = parser.parse(responseBody).getAsJsonObject();

            if (!root.has("choices") || root.getAsJsonArray("choices").size() == 0) {
                logger.warn("[LLM] No choices in response: {}", 
                    responseBody.length() > 500 ? responseBody.substring(0, 500) : responseBody);
                throw new IOException("No choices in OpenRouter response");
            }

            JsonObject choice = root.getAsJsonArray("choices").get(0).getAsJsonObject();
            JsonObject message = choice.getAsJsonObject("message");
            String content = message.get("content").getAsString();
            
            // Log the full content for debugging
            logger.info("[LLM] Raw content from LLM (length={}): \n{}", content.length(), content);

            // Clean up potential markdown code blocks
            content = content.trim();
            if (content.startsWith("```json")) {
                content = content.substring(7);
            } else if (content.startsWith("```")) {
                content = content.substring(3);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
            content = content.trim();

            LLMEventSpec spec = GSON.fromJson(content, LLMEventSpec.class);
            logger.info("[LLM] Parsed spec: name={}, descriptions={}, options={}", 
                spec.name, 
                spec.descriptions == null ? 0 : spec.descriptions.size(),
                spec.options == null ? 0 : spec.options.size());
            
            return spec;

        } catch (Exception e) {
            logger.warn("[LLM] Failed to parse OpenRouter response: {}", e.getMessage());
            logger.warn("[LLM] Response preview: {}", 
                responseBody.length() > 500 ? responseBody.substring(0, 500) : responseBody);
            throw new IOException("Failed to parse LLM response: " + e.getMessage(), e);
        }
    }
}
