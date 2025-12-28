package tuner.misc;

import basemod.abstracts.CustomSavableRaw;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import tuner.interfaces.FullArtSubscriber;

import java.util.HashMap;

public class CustomSave implements CustomSavableRaw {
    public JsonElement onSaveRaw() {
        Gson gson = new Gson();
        return gson.toJsonTree(FullArtSubscriber.FullArtsAvail);
    }

    public void onLoadRaw(JsonElement jsonElement) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Boolean>>() {}.getType();
        Map<String, Boolean> model = gson.fromJson(jsonElement, type);
        if (model == null)
            return;
        FullArtSubscriber.FullArtsAvail.clear();
        FullArtSubscriber.FullArtsAvail.putAll(model);
    }

    public static class Model {
        public Map<String, Boolean> FullArtsAvail = new HashMap<>();
    }
}