package tuner.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import tuner.helpers.ModHelper;

import java.util.ArrayList;
import java.util.Iterator;

public class RemoveAction extends AbstractGameAction {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("LoadAHandAction")).TEXT;

    private AbstractCard c;

    public RemoveAction(AbstractCard c) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.c = c;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || this.amount < 0) {
                this.isDone = true;
                return;
            }

            ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.drawPile.group.clone();

            for (AbstractCard card : list) {
                if (card == c) {
                    if (AbstractDungeon.player.hand.size() == 10) {
                        AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                        AbstractDungeon.player.createHandIsFullDialog();
                    } else {
                        AbstractDungeon.player.drawPile.moveToHand(c, AbstractDungeon.player.drawPile);
                    }
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();
                    AbstractDungeon.player.hand.refreshHandLayout();
                }
            }
        }

        this.tickDuration();
    }
}
