package tuner.cardDep;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;

public class TempCard extends MouldCard {
    public TempCard() {
        super(TempCard.class.getSimpleName(), 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
    }

    @Override
    public void triggerOnManualDiscard() {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                modifyCostForCombat(-1);
                this.isDone = true;
            }
        });
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TempCard();
    }
}
