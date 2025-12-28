package tuner.misc.option;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.ui.buttons.CancelButton;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import tuner.characters.Tuner;
import tuner.patches.utils.RoomPatch;
import tuner.patches.ui.SelectCardScreenPatch;

import java.util.ArrayList;
import java.util.Iterator;

import static com.badlogic.gdx.math.MathUtils.random;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class CampfireGetEffect extends AbstractGameEffect {

    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("tuner:CampfireGetEffect")).TEXT;
    public static boolean onOpenPatch = false;
    private int openedScreenCount = 0;
    private Color screenColor;

    private boolean playedSound = false;

    public CampfireGetEffect() {
        this.screenColor = AbstractDungeon.fadeColor.cpy();
        this.duration = 1F;
        this.screenColor.a = 0.0F;
        onOpenPatch = false;
        AbstractDungeon.overlayMenu.proceedButton.hide();
    }

    public void update() {
        if (!playedSound) {
            this.playedSound = true;
            int randomIndex = random.nextInt(5);
            if (AbstractDungeon.player instanceof Tuner)
                switch (((Tuner) AbstractDungeon.player).skinType) {
                    case 0:
                        String[] list1 = {"REST0_1", "REST0_2", "REST0_3", "REST0_4", "REST0_5"};
                        CardCrawlGame.sound.playA(list1[randomIndex], 0.0F);
                        break;
                    case 1:
                        String[] list2 = {"REST1_1", "REST1_2", "REST1_3", "REST1_4", "REST1_5"};
                        CardCrawlGame.sound.playA(list2[randomIndex], 0.0F);
                        break;
                    case 2:
                        String[] list3 = {"REST2_1", "REST2_2", "REST2_3", "REST2_4", "REST2_5"};
                        CardCrawlGame.sound.playA(list3[randomIndex], 0.0F);
                        break;
                }
        }

        if (!AbstractDungeon.isScreenUp) {
            this.duration -= Gdx.graphics.getDeltaTime();
            this.updateBlackScreenColor();
        }

        if (this.duration < 0.0F) {
            this.isDone = true;
            if (CampfireUI.hidden) {
                AbstractRoom.waitTimer = 0.0F;
                AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
                ((RestRoom) AbstractDungeon.getCurrRoom()).cutFireSound();
            }
        }

        if (AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractDungeon.gridSelectScreen.confirmButton.hide();
        } else AbstractDungeon.gridSelectScreen.confirmButton.show();


        //经过0.5S，一次开屏，选牌删除，动画开始
        if (this.duration < 0.5F && this.openedScreenCount == 0) {
            this.openedScreenCount = 1;
            CardGroup temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

            for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                if (c.type == Imaginary) {
                    temp.addToBottom(c);
                }
            }

            if (temp.isEmpty()) {
                this.duration = -1;
                return;
            }

            CampfireGetEffect.onOpenPatch = true;

            AbstractDungeon.gridSelectScreen.open(temp, 99, true, TEXT[0]);
            AbstractDungeon.overlayMenu.cancelButton.show(TEXT[1]);
        }


        Iterator var1;
        if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            if (this.openedScreenCount == 1) {//处理一次开屏：删牌
                this.openedScreenCount = 2;
                AbstractDungeon.overlayMenu.cancelButton.hide();
                ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();

                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    for (AbstractCard cc : list) {
                        if (cc.uuid == c.uuid) {
                            AbstractDungeon.player.masterDeck.removeCard(cc);
                        }
                    }
                }

                CampfireGetEffect.onOpenPatch = false;

                //二次开屏：选择牌升级
                int select = AbstractDungeon.gridSelectScreen.selectedCards.size();
                AbstractDungeon.gridSelectScreen.selectedCards.clear();

                CardGroup temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

                //生成可升级的牌
                for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                    if (c.type == Imaginary && c.timesUpgraded < 2) {
                        temp.addToBottom(c);
                    }
                }

                //升个级效果的预览
                RoomPatch.smithImgr = true;
                SelectCardScreenPatch.renderPatch.CardsToDisplay = new ArrayList<>();
                for (AbstractCard c : temp.group) {
                    AbstractCard cc = c.makeStatEquivalentCopy();
                    cc.upgrade();
                    cc.uuid = c.uuid;
                    SelectCardScreenPatch.renderPatch.CardsToDisplay.add(cc);
                }

                AbstractDungeon.gridSelectScreen.open(temp, select, true, TEXT[2] + select + TEXT[3]);
            } else
                //处理二次开屏：敲牌并显示特效
                if (this.openedScreenCount == 2) {
                    this.openedScreenCount = 3;
                    RoomPatch.smithImgr = false;
                    SelectCardScreenPatch.renderPatch.CardsToDisplay = null;
                    ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();

                    int effectCount = 0;
                    for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                        c.stopGlowing();
                        for (AbstractCard cc : list) {
                            if (cc.uuid == c.uuid) {
                                cc.upgrade();
                                effectCount++;
                                if (effectCount <= 10) {
                                    float x = MathUtils.random(0.2F, 0.8F) * Settings.WIDTH;
                                    float y = MathUtils.random(0.3F, 0.7F) * Settings.HEIGHT;
                                    AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy(), x, y));
                                    AbstractDungeon.topLevelEffectsQueue.add(new UpgradeShineEffect(x, y));
                                }
                            }
                        }
                    }
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();
                    ((RestRoom) AbstractDungeon.getCurrRoom()).fadeIn();
                }

        }
    }

    private void updateBlackScreenColor() {
        if (this.duration > 0.5F) {
            this.screenColor.a = Interpolation.fade.apply(1.0F, 0.0F, (this.duration - 0.5F) / 0.5F);
        } else {
            this.screenColor.a = Interpolation.fade.apply(0.0F, 1.0F, this.duration / 0.5F);
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.screenColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        if (AbstractDungeon.screen == CurrentScreen.GRID) {
            AbstractDungeon.gridSelectScreen.render(sb);
        }
    }

    public void dispose() {
    }


    @SpirePatch(clz = CancelButton.class,
            method = "update")
    public static class cancelRemove {
        @SpireInsertPatch(rloc = 44)
        public static SpireReturn Insert(CancelButton _inst) {

            if (CampfireGetEffect.onOpenPatch &&
                    AbstractDungeon.screen == CurrentScreen.GRID) {
                CampfireGetEffect.onOpenPatch = false;

                for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                    c.stopGlowing();
                }
                AbstractDungeon.gridSelectScreen.selectedCards.clear();

                if (!AbstractDungeon.gridSelectScreen.confirmScreenUp) {
                    AbstractDungeon.closeCurrentScreen();
                    if (AbstractDungeon.getCurrRoom() instanceof RestRoom) {
                        RestRoom r = (RestRoom) AbstractDungeon.getCurrRoom();
                        r.campfireUI.reopen();
                    }
                }

                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }
}
