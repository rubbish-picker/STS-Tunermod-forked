package tuner.patches.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import tuner.action.RewriteAction;
import tuner.patches.utils.RoomPatch;

import java.util.ArrayList;

import static tuner.action.RewriteAction.Anti;

public class SelectCardScreenPatch {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("RewriteAction")).TEXT;

    private static String rewriteTxt() {
        return TEXT[2] + RewriteAction.cardToRewrite.name +
                TEXT[3] + RewriteAction.cardToRewrite.name +
                TEXT[4] + RewriteAction.cardToRewrite.name + TEXT[5];
    }

    private static String antiRewriteTxt() {
        return TEXT[6] + RewriteAction.cardToRewrite.name +
                TEXT[7] + RewriteAction.cardToRewrite.name + TEXT[8];
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class updatePatch {
        public static SpireReturn Prefix(GridCardSelectScreen _inst, @ByRef float[] ___drawStartY) {
            if (RewriteAction.Rewriting || RoomPatch.smithImgr) {

                if (AbstractDungeon.currMapNode != null && !(AbstractDungeon.currMapNode.room instanceof RestRoom)) {
                    RoomPatch.smithImgr = false;
                }
                if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                    RewriteAction.Rewriting = false;
                }

                //修改神名碎片的位置
                if (_inst.targetGroup.group.size() <= 5) {
                    ___drawStartY[0] = (float) Settings.HEIGHT * 0.35F;
                } else {
                    ___drawStartY[0] = (float) Settings.HEIGHT * 0.42F;
                }

            } else {

                //重置印牌的高度
                if (_inst.targetGroup.group.size() <= 5) {
                    ___drawStartY[0] = (float) Settings.HEIGHT * 0.5F;
                } else {
                    ___drawStartY[0] = (float) Settings.HEIGHT * 0.66F;
                }

                SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }

        @SpireInsertPatch(rloc = 80)
        public static SpireReturn Insert(
                GridCardSelectScreen _inst,
                AbstractCard ___hoveredCard,
                @ByRef int[] ___cardSelectAmount,
                @ByRef int[] ___numCards) {
            if (RoomPatch.smithImgr) {
                if (___numCards[0] < ___cardSelectAmount[0]) {
                    if (_inst.selectedCards.contains(___hoveredCard)) {
                        ___hoveredCard.stopGlowing();
                        _inst.selectedCards.remove(___hoveredCard);
                        ___cardSelectAmount[0] -= 1;
                    }
                    return SpireReturn.Return();
                } else if (___numCards[0] == ___cardSelectAmount[0])
                    return SpireReturn.Return();
            }

            if (RewriteAction.Rewriting && !RewriteAction.Anti) {
                int sumOfCost = 0;
                for (AbstractCard c : _inst.selectedCards) {
                    sumOfCost += c.cost;
                }
                if (sumOfCost > RewriteAction.cardToRewrite.costForTurn) {
                    if (_inst.selectedCards.contains(___hoveredCard)) {
                        ___hoveredCard.stopGlowing();
                        _inst.selectedCards.remove(___hoveredCard);
                        ___cardSelectAmount[0] -= 1;
                    }
                }
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "render")
    public static class renderPatch {
        public static float tempCurrentX, tempCurrentY, tempScale;
        public static ArrayList<AbstractCard> CardsToDisplay;

        @SpirePostfixPatch
        public static void Postfix(GridCardSelectScreen _inst, SpriteBatch sb) {
            if (!PeekButton.isPeeking) {
                if (RewriteAction.Rewriting) {

                    tempCurrentX = RewriteAction.cardToRewrite.current_x;
                    tempCurrentY = RewriteAction.cardToRewrite.current_y;
                    tempScale = RewriteAction.cardToRewrite.drawScale;

                    //render被安定牌
                    RewriteAction.cardToRewrite.current_x = Settings.WIDTH * 0.3F;
                    RewriteAction.cardToRewrite.current_y = Settings.HEIGHT * 0.72F;
                    RewriteAction.cardToRewrite.drawScale = 0.6F;
                    RewriteAction.cardToRewrite.render(sb);

                    RewriteAction.cardToRewrite.current_x = tempCurrentX;
                    RewriteAction.cardToRewrite.current_y = tempCurrentY;
                    RewriteAction.cardToRewrite.drawScale = tempScale;

                    //render介绍文本
                    String txt;
                    if (Anti) txt = antiRewriteTxt();
                    else txt = rewriteTxt();

                    FontHelper.renderSmartText(sb, FontHelper.panelNameFont, txt, Settings.WIDTH * 0.5F, Settings.HEIGHT * 0.72F -
                            FontHelper.getSmartHeight(FontHelper.panelNameFont, txt, 700.0F * Settings.scale, 40.0F * Settings.scale) / 2.0F, 700.0F * Settings.scale, 40.0F * Settings.scale, Settings.CREAM_COLOR);
                }

                if (RoomPatch.smithImgr) {
                    ArrayList<AbstractCard> temp = new ArrayList<>();
                    if (CardsToDisplay != null) {
                        for (AbstractCard selectedCard : _inst.selectedCards) {
                            for (AbstractCard displayCard : CardsToDisplay) {
                                if (selectedCard.uuid == displayCard.uuid) {
                                    temp.add(displayCard);
                                }
                            }
                        }
                    }
                    for (int i = 0; i < temp.size(); i++) {
                        temp.get(i).current_x = Settings.WIDTH * 0.5F + (i + 0.5F - temp.size() / 2F) * 250F * Settings.scale;
                        temp.get(i).current_y = Settings.HEIGHT * 0.72F;
                        temp.get(i).render(sb);
                    }
                }
            }
        }
    }
}
