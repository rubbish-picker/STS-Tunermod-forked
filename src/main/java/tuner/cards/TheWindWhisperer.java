package tuner.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.GainRushPowerAction;
import tuner.interfaces.FullArtSubscriber;
import tuner.interfaces.RemoveFromDrawPileExceptDrawnSubscriber;
import tuner.interfaces.RemoveFromDrawPileSubscriber;
import tuner.powers.RushPower;

public class TheWindWhisperer extends MouldCard implements RemoveFromDrawPileSubscriber, RemoveFromDrawPileExceptDrawnSubscriber, FullArtSubscriber {
    public TheWindWhisperer() {
        super(TheWindWhisperer.class.getSimpleName(), -2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void removeFromDrawPile() {
        addToBot(new GainEnergyAction(1));
    }

    @Override
    public void removeFromDrawPileExceptDrawn() {
        if (this.upgraded) {
            addToBot(new GainEnergyAction(1));
        }
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
        return new TheWindWhisperer();
    }
}
