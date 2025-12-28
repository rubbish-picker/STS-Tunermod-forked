package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RemoveAction;
import tuner.helpers.ModHelper;
import tuner.relics.ATRelic;

import java.util.ArrayList;

import static tuner.modCore.CardTargetEnum.MapCard;

public class SlidingStep extends MouldCard {
    public SlidingStep() {
        super(SlidingStep.class.getSimpleName(), 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            private boolean once = true;

            private CardGroup temp;

            @Override
            public void update() {
                if (!ModHelper.canRewrote()) {
                    this.isDone = true;
                    return;
                }

                if (this.once) {
                    this.once = false;
                    this.duration = Settings.ACTION_DUR_FAST;
                    temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (AbstractCard c : ModHelper.rtATgroup()) {
                        temp.addToTop(c);
                    }
                    AbstractDungeon.gridSelectScreen.open(temp, 2, true, cardStrings.EXTENDED_DESCRIPTION[0]);
                }

                if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {

                    for(AbstractCard c : temp.group){
                        if(AbstractDungeon.gridSelectScreen.selectedCards.contains(c)){
                            //加手
                            if (AbstractDungeon.player.hand.size() == 10) {
                                AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                                AbstractDungeon.player.createHandIsFullDialog();
                            } else {
                                AbstractDungeon.player.drawPile.moveToHand(c, AbstractDungeon.player.drawPile);
                            }
                        }else{
                            //丢弃
                            AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                            GameActionManager.incrementDiscard(false);
                            c.triggerOnManualDiscard();
                        }
                    }
                    AbstractDungeon.player.hand.refreshHandLayout();
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();

                    this.isDone = true;

                }
                tickDuration();
            }
        });

        addToBot(new DrawCardAction(this.magicNumber));
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
        return new SlidingStep();
    }
}
