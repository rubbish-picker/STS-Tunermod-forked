package tuner.patches.ui;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.FtueTip;
import tuner.helpers.ConfigHelper;
import tuner.interfaces.FullArtSubscriber;

import java.util.ArrayList;

import static tuner.modCore.CardTypeEnum.Imaginary;

public class CardRewardScreenPatch {
    @SpirePatch(clz = CardRewardScreen.class, method = "open")
    public static class TutorialPatch {
        private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString("TunerFullArtTutorial");

        @SpireInsertPatch(rloc = 35)
        public static SpireReturn Insert(CardRewardScreen _inst, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
            if (ConfigHelper.activeFullAtrTutorial)
                for (AbstractCard c : cards) {
                    if (c instanceof FullArtSubscriber) {
                        AbstractDungeon.ftue = new FtueTip(tutorialStrings.TEXT[0], tutorialStrings.LABEL[0], Settings.WIDTH / 2.0F - 500.0F * Settings.scale, Settings.HEIGHT / 2.0F, c);
                        AbstractDungeon.ftue.type = FtueTip.TipType.POWER;
                        ConfigHelper.saveActiveFullAtrTutorial(false);
                    }
                }
            return SpireReturn.Continue();
        }
    }

    //唱歌碗
    @SpirePatch(clz = CardRewardScreen.class, method = "cardSelectUpdate")
    public static class Bowl {
        @SpireInsertPatch(rloc = 28)
        public static SpireReturn Insert(CardRewardScreen _inst, @ByRef boolean[] ___discovery) {
            for (AbstractCard c : _inst.rewardGroup) {
                if (c.type == Imaginary) {
                    ___discovery[0] = false;
                }
            }
            return SpireReturn.Continue();
        }
    }
}
