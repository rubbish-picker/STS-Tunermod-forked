package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.WallopEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import tuner.action.GainRushPowerAction;
import tuner.action.LoadAHandAction;
import tuner.effects.DirectShootingEffect;
import tuner.helpers.ModHelper;

public class LoadVoid extends MouldCard {
    public LoadVoid() {
        super(LoadVoid.class.getSimpleName(), 0, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 6;
        this.cardsToPreview = new VoidCard();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ModHelper.loadACard(new VoidCard(), null);
                ModHelper.loadACard(new VoidCard(), null);
                this.isDone = true;
            }
        });
        addToBot(new AbstractGameAction() {
            private final DamageInfo info;

            {
                this.info = new DamageInfo(p, LoadVoid.this.damage, LoadVoid.this.damageTypeForTurn);
                this.actionType = ActionType.DAMAGE;
                this.startDuration = Settings.ACTION_DUR_XFAST;
                this.duration = this.startDuration;
                setValues(m, this.info);
            }

            @Override
            public void update() {
                if (this.shouldCancelAction()) {
                    this.isDone = true;
                } else {
                    this.tickDuration();
                    if (this.isDone) {
                        AbstractDungeon.effectList.add(new DirectShootingEffect(this.target.hb.cX, this.target.hb.cY));
                        this.target.damage(this.info);
                        if (this.target.lastDamageTaken > 0) {
                            addToTop(new GainRushPowerAction(LoadVoid.this, this.target.lastDamageTaken));
                        }

                        if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                            AbstractDungeon.actionManager.clearPostCombatActions();
                        } else {
                            this.addToTop(new WaitAction(0.1F));
                        }
                    }
                }
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
        return new LoadVoid();
    }
}
