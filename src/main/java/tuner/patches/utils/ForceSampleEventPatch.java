package tuner.patches.utils;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.EventHelper;
import com.badlogic.gdx.math.MathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tuner.events.LLMGeneratedEvent;
import tuner.events.TunerSampleEvent;
import tuner.helpers.ConfigHelper;
import tuner.llm.LLMEventService;
import tuner.llm.LLMEventSpec;

public class ForceSampleEventPatch {
    private static final Logger logger = LogManager.getLogger(ForceSampleEventPatch.class);

    private static final float LLM_EVENT_CHANCE = 0.20f;

    @SpirePatch(clz = EventHelper.class, method = "getEvent")
    public static class ForceSampleEvent {
        @SpirePrefixPatch
        public static SpireReturn<AbstractEvent> Prefix(String eventName) {
            try {
                int floor = AbstractDungeon.floorNum;

                if (ConfigHelper.llmEventEnabled && ConfigHelper.debugForceLLMEvent) {
                    logger.info("[LLM] debugForceLLMEvent enabled, checking cache for floor {}", floor);
                    
                    // Only check the current floor to ensure strict floor matching
                    LLMEventSpec spec = LLMEventService.getForFloor(floor);
                    
                    if (spec != null) {
                        logger.info("[LLM] Found cached spec '{}', creating LLMGeneratedEvent", spec.name);
                        return SpireReturn.Return(new LLMGeneratedEvent(spec));
                    }
                    
                    logger.info("[LLM] No cached spec found for floor {}, skipping LLM event", floor);
                    // Do NOT trigger immediate request here - it's too late for the current event
                    // and causes unnecessary API calls.
                    return SpireReturn.Continue();
                }

                if (ConfigHelper.debugForceSampleEvent) {
                    return SpireReturn.Return(new TunerSampleEvent());
                }

                if (ConfigHelper.llmEventEnabled && MathUtils.randomBoolean(LLM_EVENT_CHANCE)) {
                    LLMEventSpec spec = LLMEventService.getForFloor(floor);
                    if (spec != null) {
                        logger.info("[LLM] Random trigger: using cached spec '{}'", spec.name);
                        return SpireReturn.Return(new LLMGeneratedEvent(spec));
                    }
                }
            } catch (Exception e) {
                logger.error("[LLM] Error in ForceSampleEventPatch: {}", e.getMessage());
                return SpireReturn.Continue();
            }

            return SpireReturn.Continue();
        }
    }
}
