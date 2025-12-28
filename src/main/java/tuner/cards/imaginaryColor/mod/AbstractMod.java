package tuner.cards.imaginaryColor.mod;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import tuner.cards.MouldCard;

public abstract class AbstractMod extends AbstractCardModifier {

    public transient MouldCard owner;

    public AbstractMod(MouldCard c){
        this.owner = c;
    }

    @Override
    public boolean removeOnCardPlayed(AbstractCard card) {
        return this.owner.exhaust;
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return true;
    }

    @Override
    public String identifier(AbstractCard card) {
        return this.owner.cardID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return this;
    }
}
