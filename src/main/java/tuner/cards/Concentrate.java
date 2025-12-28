package tuner.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.GainRushPowerAction;
import tuner.action.RemoveAction;
import tuner.helpers.ModHelper;
import tuner.powers.RushPower;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Concentrate extends MouldCard {
    public Concentrate() {
        super(Concentrate.class.getSimpleName(), 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 5;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!AbstractDungeon.player.discardPile.isEmpty()) {
            addToBot(new EmptyDeckShuffleAction());
            addToBot(new ShuffleAction(AbstractDungeon.player.drawPile, false));
        }
        addToBot(new GainRushPowerAction(this, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Concentrate();
    }
}
