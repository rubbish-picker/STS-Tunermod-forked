package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RewriteAction;
import tuner.helpers.ModHelper;
import tuner.interfaces.RemoveFromDrawPileSubscriber;

import java.util.ArrayList;

public class EntropyReducer extends MouldCard {

    public EntropyReducer() {
        super(EntropyReducer.class.getSimpleName(), 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RewriteAction(this, true));
    }

    public void noRewrote() {
        AbstractDungeon.player.drawPile.moveToExhaustPile(this);
        if (!this.upgraded) {
            ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();

            for (AbstractCard c : list) {
                if (c.uuid == this.uuid) {
                    AbstractDungeon.player.masterDeck.removeCard(c);
                }
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new EntropyReducer();
    }
}
