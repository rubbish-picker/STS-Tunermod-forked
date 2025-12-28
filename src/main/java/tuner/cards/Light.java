package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.CollectPower;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;

public class Light extends MouldCard implements FullArtSubscriber {
    public Light() {
        super(Light.class.getSimpleName(), -1, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.exhaust = true;
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

                    int effect = EnergyPanel.totalCount;
                    if (Light.this.energyOnUse != -1)
                        effect = Light.this.energyOnUse;
                    if (AbstractDungeon.player.hasRelic("Chemical X")) {
                        effect += 2;
                        AbstractDungeon.player.getRelic("Chemical X").flash();
                    }
                    if (Light.this.upgraded)
                        effect += 1;

                    if (effect > 0) {
                        temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard c : ModHelper.rtATgroup()) {
                            temp.addToTop(c);
                        }
                        AbstractDungeon.gridSelectScreen.open(temp, effect, true, cardStrings.EXTENDED_DESCRIPTION[0]);
                        if (!Light.this.freeToPlayOnce)
                            AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
                    } else {
                        this.isDone = true;
                        return;
                    }
                }

                if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {

                    for (AbstractCard c : temp.group) {
                        if (AbstractDungeon.gridSelectScreen.selectedCards.contains(c)) {
                            //加费
                            if (c.costForTurn > 0)
                                addToTop(new GainEnergyAction(c.costForTurn));
                            //加手
                            if (AbstractDungeon.player.hand.size() == 10) {
                                AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                                AbstractDungeon.player.createHandIsFullDialog();
                            } else {
                                AbstractDungeon.player.drawPile.moveToHand(c, AbstractDungeon.player.drawPile);
                            }
                        }
                    }
                    AbstractDungeon.player.hand.refreshHandLayout();
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();

                    this.isDone = true;

                }
                tickDuration();
            }
        });
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
        return new Light();
    }
}
