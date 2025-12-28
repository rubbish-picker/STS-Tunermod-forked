package tuner.cards;

import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RewriteAction;
import tuner.misc.MapcardTarget;
import tuner.interfaces.FullArtSubscriber;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Merge extends MouldCard implements FullArtSubscriber {
    public Merge() {
        super(Merge.class.getSimpleName(), 0, CardType.SKILL, CardRarity.UNCOMMON, MapCard);
        this.magicNumber = this.baseMagicNumber = 3;
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoseHPAction(p, p, this.magicNumber));
        addToTop(new RewriteAction(MapcardTarget.getTarget(this)));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(-1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Merge();
    }
}
