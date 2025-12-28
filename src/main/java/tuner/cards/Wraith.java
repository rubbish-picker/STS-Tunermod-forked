package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.GainRushPowerAction;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.RushPower;

public class Wraith extends MouldCard implements FullArtSubscriber {
    public Wraith() {
        super(Wraith.class.getSimpleName(), 0, CardType.SKILL, CardRarity.COMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            public float duration = 2F;

            public void update() {
                if (this.duration == 2F) {
                    AbstractDungeon.handCardSelectScreen.open(cardStrings.EXTENDED_DESCRIPTION[0], Wraith.this.baseMagicNumber, true, true);
//                    addToBot(new WaitAction(0.25F));
                    this.duration -= 0.5F;
                    return;
                }
                if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                    if (!AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                        for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {

                            addToTop(new GainRushPowerAction(Wraith.this, 3));

                            AbstractDungeon.player.hand.moveToDiscardPile(c);
                            GameActionManager.incrementDiscard(false);
                            c.triggerOnManualDiscard();
                        }
                    }
                    AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                    this.isDone = true;
                }

                this.tickDuration();
            }

        });
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
        return new Wraith();
    }
}
