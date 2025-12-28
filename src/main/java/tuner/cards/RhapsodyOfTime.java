package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.unique.ExpertiseAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EquilibriumPower;
import com.megacrit.cardcrawl.powers.watcher.BlockReturnPower;
import tuner.effects.TunerMoveEffect;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.Draw0Power;

public class RhapsodyOfTime extends MouldCard implements FullArtSubscriber {

    public boolean once = true;

    public RhapsodyOfTime() {
        super(RhapsodyOfTime.class.getSimpleName(), 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractMonster mo = AbstractDungeon.getRandomMonster();
        if(mo != null) {
            addToBot(new VFXAction(new TunerMoveEffect(mo.hb.cX, mo.hb.cY)));

            addToBot(new ApplyPowerAction(mo, p, new BlockReturnPower(mo, this.magicNumber)));
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    CardCrawlGame.sound.play("ATTACK_FIRE");
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            boolean flag = true;
                            for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                                if (!mo.isDeadOrEscaped() && !mo.hasPower(BlockReturnPower.POWER_ID))
                                    flag = false;
                            }
                            if (flag) {
                                for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                                    if (c == RhapsodyOfTime.this)
                                        addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.drawPile, true));

                                }
                                for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
                                    if (c == RhapsodyOfTime.this)
                                        addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.discardPile, true));
                                }
                                for (AbstractCard c : AbstractDungeon.player.hand.group) {
                                    if (c == RhapsodyOfTime.this)
                                        addToTop(new ExhaustSpecificCardAction(c, AbstractDungeon.player.hand, true));
                                }
                            }
                            this.isDone = true;
                        }
                    });
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new RhapsodyOfTime();
    }
}
