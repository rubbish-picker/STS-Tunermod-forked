package tuner.patches.combat;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import tuner.cards.Wake;

public class GainEnergyActionPatch {
    @SpirePatch(clz = GainEnergyAction.class, method = "update")
    public static class pc {
        @SpireInsertPatch(rloc = 1)
        public static void Insert(GainEnergyAction _inst) {
            for(AbstractCard c :AbstractDungeon.player.discardPile.group){
                if(c instanceof Wake)((Wake)c).receivePostEnergyRecharge();
            }
        }
    }
}