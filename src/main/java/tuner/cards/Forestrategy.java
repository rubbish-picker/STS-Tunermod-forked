package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Foresight;
import com.megacrit.cardcrawl.cards.purple.ThirdEye;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RemoveAction;
import tuner.helpers.ModHelper;
import tuner.interfaces.RemoveFromDrawPileSubscriber;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Forestrategy extends MouldCard implements RemoveFromDrawPileSubscriber {
    public Forestrategy() {
        super(Forestrategy.class.getSimpleName(), 0, CardType.SKILL, CardRarity.COMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 4;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ModHelper.discardAllCard();
                this.isDone = true;
            }
        });
        if (this.upgraded) addToBot(new DrawCardAction(1));
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
        return new Forestrategy();
    }

    @Override
    public void removeFromDrawPile() {
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, this.magicNumber));
    }
}
