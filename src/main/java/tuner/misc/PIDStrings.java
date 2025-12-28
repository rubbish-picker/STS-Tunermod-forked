package tuner.misc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PIDStrings {
    public static HashMap<String, String> namePIDMap = new HashMap<>();

    public PIDStrings() {
    }

    // 解析 JSON 并将所有元素的 NAME 和 DESCRIPTION 存储到哈希表中
    public static void parseAndStore(String jsonString) {
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonObject cardObject = entry.getValue().getAsJsonObject();

            if (cardObject.has("PID")) {
                String pid = cardObject.get("PID").getAsString();
                namePIDMap.put(entry.getKey(), pid);
            } else {
                namePIDMap.put(entry.getKey(), "NULL");
            }
        }
    }

}
