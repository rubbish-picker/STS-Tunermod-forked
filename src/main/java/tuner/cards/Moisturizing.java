package tuner.cards;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.GainRushPowerAction;
import tuner.effects.FormEffect;
import tuner.powers.RushPower;

public class Moisturizing extends MouldCard {
    public Moisturizing() {
        super(Moisturizing.class.getSimpleName(), 0, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 15;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new FormEffect(p.hb.cX, p.hb.cY)));
        addToBot(new GainRushPowerAction(this, this.magicNumber));
        addToBot(new DrawCardAction(4));
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {

        if (p.drawPile.isEmpty()) this.cantUseMessage = EXTENDED_DESCRIPTION[0];

        return p.drawPile.isEmpty() && super.canUse(p, m);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(5);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Moisturizing();
    }
}
