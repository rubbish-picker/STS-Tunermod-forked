package tuner.llm;

public enum LLMEffectType {
    // Positive effects
    GAIN_GOLD,
    HEAL,
    CARD_REWARD,
    GIVE_RELIC,
    START_COMBAT,
    GAIN_MAX_HP,
    
    // Negative/hidden effects
    LOSE_GOLD,
    LOSE_HP,
    GAIN_CURSE,
    LOSE_MAX_HP,
    START_HARD_COMBAT
}
