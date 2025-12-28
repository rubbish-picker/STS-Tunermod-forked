package tuner.cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RemoveAction;
import tuner.cards.MouldCard;
import tuner.misc.MapcardTarget;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Star extends MouldCard {
    public Star() {
        super(Star.class.getSimpleName(), 0, CardType.SKILL, CardRarity.SPECIAL, MapCard, CardColor.COLORLESS);
        this.exhaust = true;
        this.selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard c = MapcardTarget.getTarget(this);
        addToBot(new RemoveAction(c));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (upgraded && c != null && c.canUpgrade()) c.upgrade();
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Star();
    }
}
