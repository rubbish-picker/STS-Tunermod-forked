package tuner.patches.tuner;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.HitboxListener;
import tuner.cards.AR;
import tuner.cards.TargetedElimination;
import tuner.powers.TuningFormPower3;

public class Mapcard implements HitboxListener {

    private final AT at;
    public AbstractCard c;
    public int index;
    public MapCardStatus status;
    public float disappearance = AT.AniTime;
    public float emergence = AT.AniTime;
    public float currentX = AT.drawStartX;
    public float drawScale = 0.5F;
    public Hitbox hb;

    public boolean rightClickStarted = false;
    public boolean rightClick = false;

    public Mapcard(AT at, AbstractCard c, int index, MapCardStatus status) {
        this.at = at;
        this.c = c;
        this.index = index;
        this.status = status;
        this.hb = new Hitbox(AT.IMG_WIDTH * 0.5F, AT.IMG_HEIGHT * 0.5F);
    }

    @Override
    public void hoverStarted(Hitbox hitbox) {
    }

    @Override
    public void startClicking(Hitbox hitbox) {
    }

    @Override
    public void clicked(Hitbox hitbox) {
//            if (AbstractDungeon.player.hasPower(TuningFormPower3.POWER_ID) && MaxCount - this.index <= AbstractDungeon.player.getPower(TuningFormPower3.POWER_ID).amount) {
        if (
                (
                        (AbstractDungeon.player.hasPower(TuningFormPower3.POWER_ID) && AbstractDungeon.player.getPower(TuningFormPower3.POWER_ID).amount > 0)
                                ||
                                (this.c instanceof TargetedElimination)
                )
                        &&
                        AbstractDungeon.actionManager.cardQueue.isEmpty() && !AbstractDungeon.player.isDraggingCard &&
                        at.cardInUseForTunerForm == null) {
            at.cardInUseForTunerForm = this;
        }
    }

    public enum MapCardStatus {
        READY, RUNNING, SUSPENDED, NULL
    }
}
