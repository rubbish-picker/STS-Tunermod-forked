package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.unique.AddCardToDeckAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class ThousandPapermadeCranes extends MouldCard {

    public ThousandPapermadeCranes() {
        super(ThousandPapermadeCranes.class.getSimpleName(), 0, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        tags.add(AbstractCard.CardTags.HEALING);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            private final String[] TEXT = (CardCrawlGame.languagePack.getUIString("ThousandPapermadeCranes")).TEXT;

            private CardGroup temp;

            {
                this.duration = Settings.ACTION_DUR_FAST;
            }

            @Override
            public void update() {
                if (this.duration == Settings.ACTION_DUR_FAST) {
                    temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (AbstractCard c : AbstractDungeon.player.hand.group) {
                        boolean ex = true;
                        for (AbstractCard cc : AbstractDungeon.player.masterDeck.group) {
                            if (cc.cardID.equals(c.cardID)) {
                                ex = false;
                                break;
                            }
                        }
                        if (ex)
                            temp.addToTop(c.makeStatEquivalentCopy());
                    }
                    for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
                        boolean ex = true;
                        for (AbstractCard cc : AbstractDungeon.player.masterDeck.group) {
                            if (cc.cardID.equals(c.cardID)) {
                                ex = false;
                                break;
                            }
                        }
                        if (ex)
                            temp.addToTop(c.makeStatEquivalentCopy());
                    }
                    for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                        boolean ex = true;
                        for (AbstractCard cc : AbstractDungeon.player.masterDeck.group) {
                            if (cc.cardID.equals(c.cardID)) {
                                ex = false;
                                break;
                            }
                        }
                        if (ex)
                            temp.addToTop(c.makeStatEquivalentCopy());
                    }
                    for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
                        boolean ex = true;
                        for (AbstractCard cc : AbstractDungeon.player.masterDeck.group) {
                            if (cc.cardID.equals(c.cardID)) {
                                ex = false;
                                break;
                            }
                        }
                        if (ex)
                            temp.addToTop(c.makeStatEquivalentCopy());
                    }

                    if (temp.isEmpty()) {
                        addToTop(new TalkAction(true, TEXT[0], 3F, 3F));
                        this.isDone = true;
                        return;
                    } else
                        AbstractDungeon.gridSelectScreen.open(temp, 1, false, cardStrings.EXTENDED_DESCRIPTION[0]);
                }

                if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                    addToTop(new AddCardToDeckAction(
                            AbstractDungeon.gridSelectScreen.selectedCards.get(0)));

                    AbstractDungeon.gridSelectScreen.selectedCards.clear();

                    if (ThousandPapermadeCranes.this.upgraded) {
                        weaken();
                        for (AbstractCard c : p.masterDeck.group)
                            if (c.uuid == ThousandPapermadeCranes.this.uuid && c.upgraded)
                                ((ThousandPapermadeCranes) c).weaken();
                    } else {
                        addToTop(new AbstractGameAction() {
                            @Override
                            public void update() {
                                ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();
                                for (AbstractCard c : list) {
                                    if (c.uuid == ThousandPapermadeCranes.this.uuid) {
                                        AbstractDungeon.player.masterDeck.removeCard(c);
                                    }
                                }
                                this.isDone = true;
                            }
                        });
                    }

                    this.isDone = true;
                }


                tickDuration();
            }
        });
    }

    public void weaken() {
        this.timesUpgraded--;
        this.upgraded = false;
        this.name = this.cardStrings.NAME;
        this.initializeTitle();
        this.rawDescription = DESCRIPTION;
        this.initializeDescription();
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
        return new ThousandPapermadeCranes();
    }
}
