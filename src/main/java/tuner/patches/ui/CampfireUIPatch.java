package tuner.patches.ui;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import tuner.misc.option.OptionGainMaxHp;

import java.util.ArrayList;

/**
 * Always add a campfire option in RestRoom campfires.
 */
public class CampfireUIPatch {

    @SpirePatch(clz = CampfireUI.class, method = "initializeButtons")
    public static class AddGainMaxHpOption {
        @SpirePostfixPatch
        @SuppressWarnings("unchecked")
        public static void Postfix(CampfireUI __instance) {
            // CampfireUI is only used in RestRoom, but keep a cheap guard anyway.
            if (AbstractDungeon.getCurrRoom() == null) {
                return;
            }

            ArrayList<AbstractCampfireOption> buttons = ReflectionHacks.getPrivate(__instance, CampfireUI.class, "buttons");
            if (buttons == null) {
                return;
            }

            // Avoid duplicates if some other patch/relic also adds it.
            for (AbstractCampfireOption o : buttons) {
                if (o instanceof OptionGainMaxHp) {
                    return;
                }
            }

            buttons.add(new OptionGainMaxHp(true));
        }
    }
}
