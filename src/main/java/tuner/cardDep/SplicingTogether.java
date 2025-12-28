package tuner.cardDep;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;

public class SplicingTogether extends MouldCard {
    public SplicingTogether() {
        super(SplicingTogether.class.getSimpleName(), 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!AbstractDungeon.player.discardPile.isEmpty()) {
            addToBot(new EmptyDeckShuffleAction());
            addToBot(new ShuffleAction(AbstractDungeon.player.drawPile, false));
        }

//        addToBot(new ReloadAbsorbAction(p));

        if (this.upgraded)
            addToBot(new DrawCardAction(this.magicNumber));
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
        return new SplicingTogether();
    }
}
