package tuner.llm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tuner.helpers.ConfigHelper;

/**
 * Service for managing LLM-generated event prefetching and caching.
 * Now uses OpenRouter API directly instead of a local Python service.
 */
public final class LLMEventService {
    private static final Logger logger = LogManager.getLogger(LLMEventService.class);

    private LLMEventService() {
    }

    /**
     * Request event generation for a given floor if not already cached/requested.
     * This runs asynchronously to avoid blocking the game.
     */
    public static void requestForFloorIfNeeded(int floor) {
        if (!ConfigHelper.llmEventEnabled) return;
        if (floor <= 0) return;

        // Check if API key is configured
        if (ConfigHelper.llmApiKey == null || ConfigHelper.llmApiKey.trim().isEmpty()) {
            logger.debug("LLM event skipped: API key not configured");
            return;
        }

        if (!LLMEventCache.markRequested(floor)) {
            logger.info("[LLM] Floor {} already requested or cached, skipping", floor);
            return;
        }

        logger.info("[LLM] Starting prefetch for floor {}", floor);

        LLMRunSnapshot snap = LLMRunSnapshot.capture();
        new Thread(() -> {
            try {
                // Use OpenRouter API directly
                LLMEventSpec spec = OpenRouterClient.generateEventSpec(snap, floor);
                LLMEventSpec normalized = LLMEventValidator.normalize(spec);
                if (normalized != null) {
                    // Tag spec with intended floor to avoid accidental use on wrong floors
                    normalized.intendedFloor = floor;
                    LLMEventCache.put(floor, normalized);
                    logger.info("LLM event spec cached for floor {} (name={}, options={})", floor,
                            normalized.name,
                            normalized.options == null ? 0 : normalized.options.size());
                } else {
                    logger.warn("LLM event spec invalid after normalize for floor {}", floor);
                }
            } catch (Exception e) {
                // Do not Allow retry on next prefetch attempt
                logger.warn("LLM event fetch failed for floor {}: {}", floor, e.toString());
            }
        }, "tuner-llm-event-prefetch").start();
    }

    /**
     * Get the cached event spec for a floor, or null if not available.
     */
    public static LLMEventSpec getForFloor(int floor) {
        LLMEventSpec spec = LLMEventCache.get(floor);
        if (spec == null) return null;
        // If spec has an intendedFloor, ensure it matches the requested floor
        if (spec.intendedFloor != null && spec.intendedFloor.intValue() != floor) {
            logger.warn("[LLM] Spec for floor {} has intendedFloor {} - rejecting as not applicable", floor, spec.intendedFloor);
            return null;
        }
        return spec;
    }

    /**
     * Check if LLM events are properly configured and enabled.
     */
    public static boolean isAvailable() {
        return ConfigHelper.llmEventEnabled &&
               ConfigHelper.llmApiKey != null &&
               !ConfigHelper.llmApiKey.trim().isEmpty();
    }
}
