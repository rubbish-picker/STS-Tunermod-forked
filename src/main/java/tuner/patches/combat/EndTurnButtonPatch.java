package tuner.patches.combat;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;


public class EndTurnButtonPatch {
	public static boolean ended = false;
	@SpirePatch(clz = EndTurnButton.class, method = "disable", paramtypez = {boolean.class})
	public static class SpeedTurnAction {
		@SpirePostfixPatch
		public static SpireReturn Postfix(EndTurnButton _inst, boolean isEnemyTurn) {
			EndTurnButtonPatch.ended = true;
			return SpireReturn.Continue();
		}
	}
}
