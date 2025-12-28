package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RemoveAction;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Sugong extends MouldCard {
    public Sugong() {
        super(Sugong.class.getSimpleName(), 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DiscardAction(p, p, p.hand.size(), true));
        if (ModHelper.canRewrote()) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for(AbstractCard c : ModHelper.rtATgroup()){
                        ModHelper.deckMoveToHand(c);
                    }
                    this.isDone  = true;
                }
            });
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust = false;
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Sugong();
    }
}
