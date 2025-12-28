package tuner.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RewriteAction;
import tuner.misc.MapcardTarget;

import static tuner.modCore.CardTargetEnum.MapCard;

public class ConservativeOrbit extends MouldCard {
    public ConservativeOrbit() {
        super(ConservativeOrbit.class.getSimpleName(), 1, CardType.SKILL, CardRarity.COMMON, MapCard);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new RewriteAction(MapcardTarget.getTarget(this)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ConservativeOrbit();
    }
}
