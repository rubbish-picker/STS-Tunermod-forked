//package tuner.patches.utils;
//
//import basemod.BaseMod;
//import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.EverythingFix;
//import com.badlogic.gdx.graphics.Color;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
//import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
//import com.megacrit.cardcrawl.actions.utility.UseCardAction;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@SpirePatch(clz = BaseMod.class, method = "getCardColors")
//public class BaseModPatch {
//    @SpirePrefixPatch
//    public static SpireReturn<ArrayList<AbstractCard.CardColor>> Prefix(HashMap<AbstractCard.CardColor, Color> ___colorTrailVfxMap) {
//        ArrayList<AbstractCard.CardColor> list = (new ArrayList<>(___colorTrailVfxMap.keySet()))
//                .stream().filter(c -> {
////                    实际运行时间早于EverythingFix
////                    try {
////                        Field f = EverythingFix.Fields.class.getDeclaredField("noLibraryTypes");
////                        f.setAccessible(true);
////                        Set<AbstractCard.CardColor> s =  (Set<AbstractCard.CardColor>)f.get(null);
////                        return !s.contains(c);
////                    } catch (NoSuchFieldException | IllegalAccessException e) {
////                        e.printStackTrace();
////                        throw new RuntimeException(e);
////                    }
//                    return !(c.name().equals("ImaginaryColor"));
//                }).collect(Collectors.toCollection(ArrayList::new));
//
//        return SpireReturn.Return(list);
//    }
//}
