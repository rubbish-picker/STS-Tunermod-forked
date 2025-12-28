package tuner.patches.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import com.megacrit.cardcrawl.screens.stats.RunData;

import basemod.ReflectionHacks;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import tuner.modCore.CardColorEnum;

public class RunHistoryScreenPatch {
	private static final Color IMAGINARY_COLOR = Settings.PURPLE_COLOR.cpy();

	private static String getColorHex(Color color) {
		return String.format("#%02x%02x%02x",
				(int) (color.r * 255), (int) (color.g * 255), (int) (color.b * 255));
	}

	@SpirePatch(clz = RunHistoryScreen.class, method = "reloadCards", paramtypez = {RunData.class})
	public static class ReloadCardsPatch {
		@SpirePostfixPatch
		public static void Postfix(RunHistoryScreen _inst, RunData runData) {
			ArrayList<TinyCard> cards = ReflectionHacks.getPrivate(_inst, RunHistoryScreen.class, "cards");

			if (cards.stream().noneMatch(c -> c.card.color == CardColorEnum.ImaginaryColor))
				return;

			HashMap<AbstractCard.CardRarity, Integer> eleph = new HashMap<>();
			HashMap<AbstractCard.CardRarity, Integer> nonEleph = new HashMap<>();

			for (TinyCard card : cards) {
				if (card.card.color == CardColorEnum.ImaginaryColor)
					eleph.put(card.card.rarity, eleph.getOrDefault(card.card.rarity, 0) + card.count);
				else
					nonEleph.put(card.card.rarity, nonEleph.getOrDefault(card.card.rarity, 0) + card.count);
			}

			String label = ReflectionHacks.getPrivateStatic(RunHistoryScreen.class, "COUNT_WITH_LABEL");

			StringBuilder sb = new StringBuilder();

			for (AbstractCard.CardRarity rarity : AbstractCard.CardRarity.values())
				if (nonEleph.containsKey(rarity)) {
					if (sb.length() > 0)
						sb.append(", ");

					sb.append(String.format(label, nonEleph.get(rarity),
							ReflectionHacks.privateMethod(RunHistoryScreen.class, "rarityLabel",
									AbstractCard.CardRarity.class)
									.invoke(_inst, rarity)));
				}

			String first = sb.toString();

			sb = new StringBuilder();

			for (AbstractCard.CardRarity rarity : AbstractCard.CardRarity.values())
				if (eleph.containsKey(rarity)) {
					if (sb.length() > 0)
						sb.append(", ");

					sb.append(String.format(label, eleph.get(rarity),
							ReflectionHacks.privateMethod(RunHistoryScreen.class, "rarityLabel",
									AbstractCard.CardRarity.class)
									.invoke(_inst, rarity)));
				}

			String second = "+ " + sb.toString();
			second = Arrays.stream(second.split(" "))
					.map(s -> "[" + getColorHex(IMAGINARY_COLOR) + "]" + s)
					.reduce((s1, s2) -> s1 + " " + s2)
					.orElse("");

			ReflectionHacks.setPrivate(_inst, RunHistoryScreen.class,
					"cardCountByRarityString", first + " " + second);
		}
	}

	@SpirePatch(clz = RunHistoryScreen.class, method = "renderDeck",
			paramtypez = {SpriteBatch.class, float.class, float.class})
	public static class RenderDeckPatch {
		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctBehavior) throws CannotCompileException, PatchingException {
				return LineFinder.findInOrder(ctBehavior,
						new Matcher.MethodCallMatcher(RunHistoryScreen.class,
								"renderSubHeadingWithMessage"));
			}
		}

		@SpireInsertPatch(locator = Locator.class, localvars = {"mainText"})
		public static void Insert(RunHistoryScreen _inst, SpriteBatch sb,
								  float x, float y, @ByRef String[] mainText) {
			ArrayList<TinyCard> cards = ReflectionHacks.getPrivate(_inst,
					RunHistoryScreen.class, "cards");

			if (cards.stream().noneMatch(c -> c.card.color == CardColorEnum.ImaginaryColor))
				return;

			int eleph = 0, nonEleph = 0;

			for (TinyCard card : cards) {
				if (card.card.color == CardColorEnum.ImaginaryColor)
					eleph += card.count;
				else
					nonEleph += card.count;
			}

			String label = ReflectionHacks.getPrivateStatic(RunHistoryScreen.class,
					"LABEL_WITH_COUNT_IN_PARENS");

			label = label.replace("%2$d", "%2$s");

			mainText[0] = String.format(label, RunHistoryScreen.TEXT[9],
					nonEleph + "[" + getColorHex(IMAGINARY_COLOR) + "]+" + eleph + "[]");
		}
	}
}
