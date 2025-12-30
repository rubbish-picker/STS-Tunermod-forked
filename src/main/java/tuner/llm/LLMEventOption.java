package tuner.llm;

import java.util.ArrayList;

public class LLMEventOption {
    public String text;
    public ArrayList<LLMEffect> effects = new ArrayList<>();
    
    // Hidden negative effects - only executed AFTER the visible effects
    public ArrayList<LLMEffect> hiddenEffects = new ArrayList<>();
    
    // Text revealed to player after choosing this option (shows the trap)
    public String revealText;
}
