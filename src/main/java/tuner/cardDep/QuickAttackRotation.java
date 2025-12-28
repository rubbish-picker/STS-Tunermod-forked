package tuner.cardDep;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.helpers.ModHelper;

import java.util.ArrayList;

public class QuickAttackRotation extends MouldCard {
    public QuickAttackRotation() {
        super(QuickAttackRotation.class.getSimpleName(), 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) p.hand.group.clone();

        for (AbstractCard c : list) {
            addToBot(new AbstractGameAction() {
                public float duration = Settings.ACTION_DUR_FAST;

                @Override
                public void update() {
                    if (this.duration == Settings.ACTION_DUR_FAST) {
                        ModHelper.loadACard(c, p.hand);
                    }
                    tickDuration();
                }
            });
        }

        if (this.upgraded)
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    ModHelper.discardAllCard();
                    this.isDone = true;
                }
            });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new QuickAttackRotation();
    }
}
