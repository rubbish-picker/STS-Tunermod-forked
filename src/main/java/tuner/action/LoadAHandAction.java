package tuner.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import tuner.helpers.ModHelper;

import java.util.ArrayList;
import java.util.Iterator;

public class LoadAHandAction extends AbstractGameAction {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("LoadAHandAction")).TEXT;

    public boolean anyNum;
    public boolean canPickZero;

    public LoadAHandAction(int amount, boolean anyNum, boolean canPickZero) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.amount = amount;
        this.anyNum = anyNum;
        this.canPickZero = canPickZero;
    }

    public LoadAHandAction(int amount) {
        this(amount, false, false);
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || this.amount < 0) {
                this.isDone = true;
                return;
            }

            if (AbstractDungeon.player.hand.size() <= this.amount && !anyNum && !canPickZero) {
                this.amount = AbstractDungeon.player.hand.size();
                int size = AbstractDungeon.player.hand.size();

                ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.hand.group.clone();

                for (int i = 0; i < size; ++i) {
                    AbstractCard c = list.get(i);
                    ModHelper.loadACard(c, AbstractDungeon.player.hand);
                }

                AbstractDungeon.player.hand.applyPowers();
                this.isDone = true;
                return;
            }

            AbstractDungeon.handCardSelectScreen.open(TEXT[0] + this.amount + TEXT[1], this.amount, this.anyNum, this.canPickZero);
            AbstractDungeon.player.hand.applyPowers();
        } else {
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                Iterator var4 = AbstractDungeon.handCardSelectScreen.selectedCards.group.iterator();

                ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.handCardSelectScreen.selectedCards.group.clone();
                for (AbstractCard c : list) {
                    ModHelper.loadACard(c, AbstractDungeon.handCardSelectScreen.selectedCards);
                }

                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            }
        }

        this.tickDuration();

    }
}
