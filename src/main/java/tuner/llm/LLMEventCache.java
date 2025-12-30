package tuner.llm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LLMEventCache {
    private static final Map<Integer, LLMEventSpec> byFloor = new HashMap<>();
    private static final Set<Integer> requestedFloors = new HashSet<>();

    private LLMEventCache() {
    }

    public static synchronized boolean markRequested(int floor) {
        return requestedFloors.add(floor);
    }

    public static synchronized void unmarkRequested(int floor) {
        requestedFloors.remove(floor);
    }

    public static synchronized void put(int floor, LLMEventSpec spec) {
        if (spec == null) return;
        byFloor.put(floor, spec);
    }

    public static synchronized LLMEventSpec get(int floor) {
        return byFloor.get(floor);
    }

    public static synchronized void clear() {
        byFloor.clear();
        requestedFloors.clear();
    }
}
