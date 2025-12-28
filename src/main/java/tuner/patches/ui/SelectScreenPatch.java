package tuner.patches.ui;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import tuner.misc.charSelect.SkinSelectScreen;

import static tuner.modCore.PlayerEnum.Tuner_CLASS;

public class SelectScreenPatch {
    public static boolean isPuzzlerSelected() {
        return (CardCrawlGame.chosenCharacter == Tuner_CLASS && ((Boolean) ReflectionHacks.getPrivate(CardCrawlGame.mainMenuScreen.charSelectScreen, CharacterSelectScreen.class, "anySelected")).booleanValue());
    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "render")
    public static class RenderButtonPatch {
        public static void Postfix(CharacterSelectScreen _inst, SpriteBatch sb) {
            if (SelectScreenPatch.isPuzzlerSelected())
                if (SkinSelectScreen.Inst != null) {
                    SkinSelectScreen.Inst.render(sb);
                }
        }

        @SpireInsertPatch(rloc = 78)
        public static void Insert(CharacterSelectScreen _inst, SpriteBatch sb) {
            if (SelectScreenPatch.isPuzzlerSelected())
                if (SkinSelectScreen.Inst != null) {
                    SkinSelectScreen.Inst.renderVideo(sb);
                }
        }

    }

    @SpirePatch(clz = CharacterSelectScreen.class, method = "update")
    public static class UpdateButtonPatch {
        public static void Prefix(CharacterSelectScreen _inst) {
            if (SelectScreenPatch.isPuzzlerSelected())
                if (SkinSelectScreen.Inst != null) {
                    SkinSelectScreen.Inst.update();
                }
        }
    }
}