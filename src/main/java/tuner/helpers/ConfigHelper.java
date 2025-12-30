package tuner.helpers;

import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import tuner.modCore.AstrographTuner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class ConfigHelper {

    public static Properties SSAModDefaultSettings = new Properties();
    public static SpireConfig config = null;
    public static boolean otherObtainDestroyer = false;
    public static boolean activeTutorials = true;
    public static boolean activeFullAtrTutorial = true;
    public static boolean dontLoad3d = false;
    public static boolean difficultMod = false;
    public static int skinIndexSaved = 0;
    public static boolean imgSelectSfx = true;
    public static boolean debugForceSampleEvent = false;
    public static boolean llmEventEnabled = true;
    public static boolean debugForceLLMEvent = true;
    public static String llmEndpoint = "https://openrouter.ai/api/v1/chat/completions";
    public static String llmApiKey = "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";
    public static String llmModel = "google/gemini-3-flash-preview";

    public static void tryCreateConfig() {
        String configFileName = "HinaConfig";
        try {
            config = new SpireConfig("Hina", configFileName);
        } catch (IOException e) {
            AstrographTuner.logger.warn("+++++++++++++++++++++" + e + "++++++++++++++++++++++");
            config = null;
        }

        if (config != null) {
            otherObtainDestroyer = (config.has("otherObtainDestroyer") && config.getBool("otherObtainDestroyer"));
            activeTutorials = (!config.has("activeTutorials") || config.getBool("activeTutorials"));
            activeFullAtrTutorial = (!config.has("activeFullAtrTutorial") || config.getBool("activeFullAtrTutorial"));
            dontLoad3d = (config.has("dontLoad3d") && config.getBool("dontLoad3d"));
            difficultMod = (config.has("difficultMod") && config.getBool("difficultMod"));
            debugForceSampleEvent = (config.has("debugForceSampleEvent") && config.getBool("debugForceSampleEvent"));

            if (config.has("skinIndexSaved")) {
                skinIndexSaved = config.getInt("skinIndexSaved");
            } else skinIndexSaved = 0;

            imgSelectSfx = (!config.has("imgSelectSfx") || config.getBool("imgSelectSfx"));
            llmEventEnabled = (config.has("llmEventEnabled") && config.getBool("llmEventEnabled"));
            debugForceLLMEvent = (config.has("debugForceLLMEvent") && config.getBool("debugForceLLMEvent"));
            if (config.has("llmEndpoint")) {
                llmEndpoint = config.getString("llmEndpoint");
            }
            if (config.has("llmApiKey")) {
                llmApiKey = config.getString("llmApiKey");
            }
            if (config.has("llmModel")) {
                llmModel = config.getString("llmModel");
            }
        }
    }

    public static void trySaveConfig(SpireConfig config) {
        try {
            config.save();
        } catch (IOException e) {
            AstrographTuner.logger.warn(e);
        }
    }

    public static void saveActiveFullAtrTutorial(boolean b) {
        if(config != null) {
            activeFullAtrTutorial = b;
            config.setBool("activeFullAtrTutorial", activeFullAtrTutorial);
            trySaveConfig(config);
        }
    }

    public static void saveSkinIndexSaved(int i) {
        if(config != null) {
            skinIndexSaved = i;
            config.setInt("skinIndexSaved", skinIndexSaved);
            trySaveConfig(config);
        }
    }

    //初始化设置
    public static ModPanel initSettings(boolean inconfig) {
        if (config == null)
            tryCreateConfig();

        AstrographTuner.logger.info("===============加载设置===============");
        String configPath = "";

        if (Settings.language == Settings.GameLanguage.ZHS || Settings.language == Settings.GameLanguage.ZHT) {
            configPath = "tunerResources/localization/tuner_config_zh.json";
        } else if (Settings.language == Settings.GameLanguage.KOR)
            configPath = "tunerResources/localization/tuner_config_kr.json";
        else {
            configPath = "tunerResources/localization/tuner_config_eng.json";
        }

        Gson gson = new Gson();
        String json = Gdx.files.internal(configPath).readString(String.valueOf(StandardCharsets.UTF_8));
        Type configType = (new TypeToken<Map<String, String>>() {
        }).getType();

        Map<String, String> configStrings = gson.fromJson(json, configType);


        ModPanel modPanel = new ModPanel();

        if (inconfig) {
            float yPos = 750.0F;
            ModLabeledToggleButton otherObtainDestroyerButton =
                    new ModLabeledToggleButton(configStrings.get("otherObtainDestroyer"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, otherObtainDestroyer, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            otherObtainDestroyer = button.enabled;
                            config.setBool("otherObtainDestroyer", otherObtainDestroyer);
                            trySaveConfig(config);
                        }
                    });

            yPos -= 50.0F;
            ModLabeledToggleButton activeTutorialsButton =
                    new ModLabeledToggleButton(configStrings.get("activeTutorials"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, activeTutorials, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            activeTutorials = button.enabled;
                            config.setBool("activeTutorials", activeTutorials);
                            trySaveConfig(config);
                        }
                    });
            yPos -= 50.0F;
            ModLabeledToggleButton dontLoad3dButton =
                    new ModLabeledToggleButton(configStrings.get("dontLoad3d"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, dontLoad3d, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            dontLoad3d = button.enabled;
                            config.setBool("dontLoad3d", dontLoad3d);
                            trySaveConfig(config);
                        }
                    });
            yPos -= 50.0F;
            ModLabeledToggleButton difficultModButton =
                    new ModLabeledToggleButton(configStrings.get("difficultMod"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, difficultMod, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            difficultMod = button.enabled;
                            config.setBool("difficultMod", difficultMod);
                            trySaveConfig(config);
                        }
                    });

            yPos -= 50.0F;
            ModLabeledToggleButton imgSelectSfxButton =
                    new ModLabeledToggleButton(configStrings.get("imgSelectSfx"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, imgSelectSfx, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            imgSelectSfx = button.enabled;
                            config.setBool("imgSelectSfx", imgSelectSfx);
                            trySaveConfig(config);
                        }
                    });

            yPos -= 50.0F;
            ModLabeledToggleButton debugForceSampleEventButton =
                    new ModLabeledToggleButton(configStrings.get("debugForceSampleEvent"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, debugForceSampleEvent, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            debugForceSampleEvent = button.enabled;
                            config.setBool("debugForceSampleEvent", debugForceSampleEvent);
                            trySaveConfig(config);
                        }
                    });

            yPos -= 50.0F;
            ModLabeledToggleButton llmEventEnabledButton =
                    new ModLabeledToggleButton(configStrings.get("llmEventEnabled"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, llmEventEnabled, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            llmEventEnabled = button.enabled;
                            config.setBool("llmEventEnabled", llmEventEnabled);
                            trySaveConfig(config);
                        }
                    });

            yPos -= 50.0F;
            ModLabeledToggleButton debugForceLLMEventButton =
                    new ModLabeledToggleButton(configStrings.get("debugForceLLMEvent"), 350.0F, yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, debugForceLLMEvent, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            debugForceLLMEvent = button.enabled;
                            config.setBool("debugForceLLMEvent", debugForceLLMEvent);
                            trySaveConfig(config);
                        }
                    });

            modPanel.addUIElement(otherObtainDestroyerButton);
            modPanel.addUIElement(activeTutorialsButton);
            modPanel.addUIElement(dontLoad3dButton);
            modPanel.addUIElement(difficultModButton);
            modPanel.addUIElement(imgSelectSfxButton);
            modPanel.addUIElement(debugForceSampleEventButton);
            modPanel.addUIElement(llmEventEnabledButton);
            modPanel.addUIElement(debugForceLLMEventButton);
        } else {
            ModLabeledToggleButton otherObtainDestroyerButton =
                    new ModLabeledToggleButton(configStrings.get("otherObtainDestroyer2"),
                            Settings.WIDTH * 0.1F / Settings.scale, Settings.HEIGHT * 0.375F / Settings.scale,
                            Settings.CREAM_COLOR, FontHelper.cardTypeFont, otherObtainDestroyer, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            otherObtainDestroyer = button.enabled;
                            config.setBool("otherObtainDestroyer", otherObtainDestroyer);
                            trySaveConfig(config);
                        }
                    });
            ModLabeledToggleButton dontLoad3dButton =
                    new ModLabeledToggleButton(configStrings.get("dontLoad3d"),
                            Settings.WIDTH * 0.1F / Settings.scale, Settings.HEIGHT * 0.375F / Settings.scale - 50F,
                            Settings.CREAM_COLOR, FontHelper.cardTypeFont, dontLoad3d, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            dontLoad3d = button.enabled;
                            config.setBool("dontLoad3d", dontLoad3d);
                            trySaveConfig(config);
                        }
                    });
            ModLabeledToggleButton difficultModButton =
                    new ModLabeledToggleButton(configStrings.get("difficultMod"),
                            Settings.WIDTH * 0.1F / Settings.scale, Settings.HEIGHT * 0.375F / Settings.scale - 100F,
                            Settings.CREAM_COLOR, FontHelper.cardTypeFont, difficultMod, modPanel, label -> {
                    }, button -> {
                        if (config != null) {
                            difficultMod = button.enabled;
                            config.setBool("difficultMod", difficultMod);
                            trySaveConfig(config);
                        }
                    });

            modPanel.addUIElement(otherObtainDestroyerButton);
            modPanel.addUIElement(dontLoad3dButton);
            modPanel.addUIElement(difficultModButton);
        }
        AstrographTuner.logger.info("===============设置加载完毕===============");
        return modPanel;
    }
}
