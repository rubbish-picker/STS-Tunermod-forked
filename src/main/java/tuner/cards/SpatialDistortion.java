package tuner.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BlurPower;
import tuner.action.GainRushPowerAction;
import tuner.powers.RushPower;

public class SpatialDistortion extends MouldCard {
    public SpatialDistortion() {
        super(SpatialDistortion.class.getSimpleName(), 2, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (p.currentBlock > 0) {
            addToBot(new GainRushPowerAction(this, p.currentBlock));
        }
        if (this.upgraded) {
            if (p.hasPower(RushPower.POWER_ID)) {
                addToBot(new GainBlockAction(p, p.getPower(RushPower.POWER_ID).amount));
            }
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
        return new SpatialDistortion();
    }
}
