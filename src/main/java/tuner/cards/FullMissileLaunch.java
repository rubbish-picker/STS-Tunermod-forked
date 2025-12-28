package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.CutThroughFate;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.effects.DirectShootingEffect;
import tuner.helpers.ModHelper;
import tuner.powers.RushPower;

public class FullMissileLaunch extends MouldCard {
    public FullMissileLaunch() {
        super(FullMissileLaunch.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 10;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new DirectShootingEffect(m.hb.cX, m.hb.cY)));
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.NONE));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {

                if (ModHelper.canRewrote()) {
                    for (AbstractCard c : ModHelper.rtATgroup()) {
                        if (c.type == CardType.STATUS || c.type == CardType.CURSE) {
                            AbstractDungeon.player.drawPile.moveToExhaustPile(c);
                        } else AbstractDungeon.player.drawPile.moveToDiscardPile(c);
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
            upgradeDamage(4);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FullMissileLaunch();
    }
}
