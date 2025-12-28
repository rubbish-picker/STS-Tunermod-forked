package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.effects.DirectShootingEffect;
import tuner.helpers.ModHelper;
import tuner.interfaces.NonDiscardableSubscriber;
import tuner.powers.RushPower;

import java.util.Iterator;

public class BreakingThroughTheArmy extends MouldCard implements NonDiscardableSubscriber {
    public BreakingThroughTheArmy() {
        super(BreakingThroughTheArmy.class.getSimpleName(), 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = 7;
        this.damage = this.baseDamage = 7;
    }


    @Override
    public void onUseCard() {
        ModHelper.loadACard(this, null);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new DirectShootingEffect(m.hb.cX, m.hb.cY)));
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.NONE));

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                Iterator var1 = GetAllInBattleInstances.get(BreakingThroughTheArmy.this.uuid).iterator();

                while (var1.hasNext()) {
                    AbstractCard c = (AbstractCard) var1.next();
                    c.baseDamage += BreakingThroughTheArmy.this.baseMagicNumber;
                    if (c.baseDamage < 0) {
                        c.baseDamage = 0;
                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
            upgradeMagicNumber(2);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BreakingThroughTheArmy();
    }
}
