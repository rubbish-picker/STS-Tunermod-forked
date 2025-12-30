package tuner.llm;

import java.util.ArrayList;

public class LLMEventSpec {
    // Optional: the floor this spec was generated for (set by prefetching code)
    public Integer intendedFloor = null;

    public String name;
    public ArrayList<String> descriptions = new ArrayList<>();
    public ArrayList<LLMEventOption> options = new ArrayList<>();
}
