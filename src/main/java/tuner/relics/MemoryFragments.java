package tuner.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import tuner.helpers.ModHelper;

public class MemoryFragments extends CustomRelic {
    public static final String ID = ModHelper.makeID(MemoryFragments.class.getSimpleName());
    private static final String IMG = "tunerResources/img/relics/MemoryFragments.png";
    private static final String IMG_OTL = "tunerResources/img/relics/outline/MemoryFragments.png";
    private boolean RclickStart = false;
    private boolean Rclick = false;
    private boolean used = false;

    public MemoryFragments() {
        super(ID, ImageMaster.loadImage(IMG), ImageMaster.loadImage(IMG_OTL), RelicTier.STARTER, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStartPreDraw() {
        used = false;
        this.grayscale = false;
    }

    @Override
    public void atTurnStart() {
        if (!used) addToBot(new GainEnergyAction(1));
    }

    @Override
    public void onPlayerEndTurn() {
        if (EnergyPanel.totalCount <= 0) {
            used = true;
            this.grayscale = true;
        }
    }

    public void update() {
        super.update();
        if (AbstractDungeon.currMapNode != null
                && AbstractDungeon.player != null
                && (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT) {
            if (this.RclickStart && InputHelper.justReleasedClickRight) {
                if (this.hb.hovered) {
                    this.Rclick = true;
                }
                this.RclickStart = false;
            }
            if (this.isObtained && this.hb != null && this.hb.hovered && InputHelper.justClickedRight) {

                this.RclickStart = true;
            }
            if (this.Rclick) {
                this.Rclick = false;
                onRightClick();
            }
        }
    }

    private void onRightClick() {
//        ModHelper.logger.info("============目标卡死了吗？============");
//        ModHelper.logger.info("所有目标的内容包括：");
//        ModHelper.logger.info(ModHelper.rtATgroup());
//        ModHelper.logger.info("old list内容为：");
//        String s = "\n";
//        for (AT.Mapcard m : ATRelic.at.oldList) {
//            s = s + "\n" + m.c.name + " " + m.index + " " + m.status;
//        }
//        ModHelper.logger.info(s);
//        ModHelper.logger.info("当前的Mapcard的内容是：");
//        ModHelper.logger.info(PatchSingleTargetInput.mapcard);
//        ModHelper.logger.info("当前的kakaa的状态是：");
//        ModHelper.logger.info(PatchSingleTargetInput.M);
//        ModHelper.logger.info("===============日志如上=============");
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new MemoryFragments();
    }
}
