package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.Predator;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import tuner.cards.colorless.Star;
import tuner.interfaces.FullArtSubscriber;
import tuner.interfaces.IntoDrawPileSubscriber;
import tuner.powers.IntelligencePreparationPower;

public class IntelligencePreparation extends MouldCard {
    public IntelligencePreparation() {
        super(IntelligencePreparation.class.getSimpleName(), 0, CardType.SKILL, CardRarity.COMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (upgraded) {
            addToBot(new ApplyPowerAction(p,p,new IntelligencePreparationPower(p,this.magicNumber)));
        } else {
            addToBot(new ApplyPowerAction(p, p, new DrawCardNextTurnPower(p, this.magicNumber)));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            this.initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new IntelligencePreparation();
    }
}
