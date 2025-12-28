package tuner.cards.colorless;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.misc.MapcardTarget;
import tuner.interfaces.FullArtSubscriber;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Feather extends MouldCard implements FullArtSubscriber {
    public Feather() {
        super(Feather.class.getSimpleName(), 0, CardType.SKILL, CardRarity.SPECIAL, MapCard, CardColor.COLORLESS);
        this.exhaust = true;
        this.selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard temp = MapcardTarget.getTarget(this);
        if (temp != null) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    if (p.drawPile.contains(temp)) {
                        p.drawPile.moveToDiscardPile(temp);
                        if (temp.canUpgrade() && Feather.this.upgraded) temp.upgrade();
                    }
                    this.isDone = true;
                }
            });
        }
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
        return new Feather();
    }
}
