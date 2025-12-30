package tuner.llm;

public class LLMEffect {
    public LLMEffectType type;

    // Generic numeric payload (e.g. gold/heal)
    public Integer amount;

    // Optional display text for UI prompts
    public String prompt;

    // For future extension (keep nullable, validated locally)
    public String rarity;
    public String pool;
    public String encounterKey;
}
