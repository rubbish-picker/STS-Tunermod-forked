package tuner.cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import tuner.action.RewriteAction;
import tuner.cards.MouldCard;
import tuner.misc.MapcardTarget;
import tuner.helpers.ModHelper;
import tuner.interfaces.OnRightClickInHandSubscriber;
import static tuner.modCore.CardTargetEnum.MapCard;

public class AorB extends MouldCard implements OnRightClickInHandSubscriber {
    public AorB() {
        super(AorB.class.getSimpleName(), 0, CardType.STATUS, CardRarity.SPECIAL, MapCard, CardColor.COLORLESS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (ModHelper.canRewrote()) {
            addToBot(new RewriteAction(MapcardTarget.getTarget(this)));
        }
    }

    @Override
    public void onRightClickInHand() {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractDungeon.player.hand.contains(AorB.this))
                    AbstractDungeon.player.hand.moveToDiscardPile(AorB.this);

                // AorB.this.onUse = false;
                this.isDone = true;
            }
        });

        if (ModHelper.canRewrote())
            addToBot(new RewriteAction(AbstractDungeon.player.drawPile.getTopCard(), true));
    }

    @Override
    public void upgrade() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new AorB();
    }
}
