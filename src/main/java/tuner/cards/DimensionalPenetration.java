package tuner.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import tuner.effects.DirectShootingEffect;
import tuner.powers.RushPower;

public class DimensionalPenetration extends MouldCard {
    public DimensionalPenetration() {
        super(DimensionalPenetration.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 9;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (m != null) {
                    AbstractDungeon.effectList.add(new DirectShootingEffect(m.hb.cX, m.hb.cY));
                    m.damage(new DamageInfo(p, damage, damageTypeForTurn));
//                    if (((m).isDying || m.currentHealth <= 0) && !m.halfDead) {
                    if (((m).isDying || m.currentHealth <= 0)) {

                        //斩杀效果
                        addToTop(new AbstractGameAction() {
                            @Override
                            public void update() {
                                boolean hasMon = false;
                                for (AbstractMonster monster : (AbstractDungeon.getCurrRoom()).monsters.monsters) {
                                    if (!monster.isDeadOrEscaped() && monster.hb.cX >= m.hb.cX) {
                                        monster.tint.color.set(Color.RED);
                                        monster.tint.changeColor(Color.WHITE.cpy());
                                        monster.damage(new DamageInfo(this.source, m.maxHealth, DamageInfo.DamageType.THORNS));
                                        hasMon = true;
                                    }
                                }
                                if(hasMon){
                                    this.addToTop(new SFXAction("ORB_LIGHTNING_EVOKE"));
                                }

                                this.isDone = true;
                            }
                        });
                        addToTop(new WaitAction(0.25F));

                    }
                    if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead())
                        AbstractDungeon.actionManager.clearPostCombatActions();
                }
                this.isDone = true;
            }
        });

    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new DimensionalPenetration();
    }
}
