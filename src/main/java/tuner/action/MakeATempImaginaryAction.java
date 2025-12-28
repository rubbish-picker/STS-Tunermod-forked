package tuner.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import tuner.relics.ATRelic;

public class MakeATempImaginaryAction extends AbstractGameAction {

    private AbstractCard c;
    private int num;

    public MakeATempImaginaryAction(AbstractCard c) {
        this(c, 1);
    }

    public MakeATempImaginaryAction(AbstractCard c, int num) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.c = c;
        this.num = num;
        UnlockTracker.markCardAsSeen(c.cardID);
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.player.hasRelic(ATRelic.ID)) {
                for (int i = 0; i < this.num; i++)
                    ATRelic.at.dorlach.dorlachGroup.addToBottom(c.makeStatEquivalentCopy());
            }
        }
        this.tickDuration();
    }
}
