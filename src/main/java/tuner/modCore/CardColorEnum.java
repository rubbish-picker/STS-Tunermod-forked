package tuner.modCore;

import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoLibraryType;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class CardColorEnum {
  @SpireEnum(name = "Hina")
  public static AbstractCard.CardColor TunerColor;

  @SpireEnum(name = "神名碎片")
//  @NoLibraryType
  public static AbstractCard.CardColor ImaginaryColor;
}