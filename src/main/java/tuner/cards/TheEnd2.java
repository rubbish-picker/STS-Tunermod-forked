package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.effects.DirectShootingEffect;
import tuner.helpers.ModHelper;
import tuner.relics.ATRelic;

public class TheEnd2 extends MouldCard {
    public TheEnd2() {
        super(TheEnd2.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 3;
        this.magicNumber = this.baseMagicNumber = 4;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        int i = 0, j = 0;
        while (i < ATRelic.at.MaxCount - 1 || j < this.magicNumber) {
            if (j < this.magicNumber) {
                addToBot(new VFXAction(new DirectShootingEffect(m.hb.cX, m.hb.cY)));
                addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                        AbstractGameAction.AttackEffect.NONE));
            }
            if (i < ATRelic.at.MaxCount - 1) {
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        if (ModHelper.canRewrote()) {
                            if (ATRelic.at.MaxCount > 1)
                                ATRelic.at.MaxCount--;
                        }
                        this.isDone = true;
                    }
                });
            }
            i++;
            j++;
        }

    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TheEnd2();
    }
}
