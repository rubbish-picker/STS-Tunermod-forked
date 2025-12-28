package tuner.cards.colorless;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RewriteAction;
import tuner.cards.MouldCard;
import tuner.misc.MapcardTarget;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Overwriting extends MouldCard {
    public Overwriting() {
        super(Overwriting.class.getSimpleName(), 0, CardType.SKILL, CardRarity.SPECIAL, MapCard, CardColor.COLORLESS);
        this.exhaust = true;
        this.selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard c = MapcardTarget.getTarget(this);
        addToBot(new RewriteAction(c, true, true));
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
        return new Overwriting();
    }
}
