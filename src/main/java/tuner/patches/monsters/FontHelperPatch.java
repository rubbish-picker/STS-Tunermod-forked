package tuner.patches.monsters;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.FontHelper;

@SuppressWarnings("unused")
public class FontHelperPatch {
	public static BitmapFont energyNumFontYellow;

	@SpirePatch(clz = FontHelper.class, method = "initialize", paramtypez = {})
	public static class InitPatch {
		@SpireInsertPatch(rloc = 192)
		public static void Insert() {
			FreeTypeFontGenerator.FreeTypeFontParameter param = ReflectionHacks.getPrivateStatic(
					FontHelper.class, "param");

			param.borderColor = new Color(0.4F, 0.4F, 0.15F, 1.0F);
			energyNumFontYellow = FontHelper.prepFont(36.0F, true);
		}
	}
}
