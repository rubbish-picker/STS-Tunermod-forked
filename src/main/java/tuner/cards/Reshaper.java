package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.colorless.Star;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;

public class Reshaper extends MouldCard implements FullArtSubscriber {
    public Reshaper() {
        super(Reshaper.class.getSimpleName(), 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.cardsToPreview = new Star();
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            private boolean once = true;

            @Override
            public void update() {
                if (this.once) {
                    this.once = false;
                    this.duration = Settings.ACTION_DUR_FAST;
                    CardGroup temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    for (AbstractCard c : p.discardPile.group) {
                        temp.addToBottom(c);
                    }

                    if (temp.isEmpty()) {
                        this.isDone = true;
                        return;
                    }

                    AbstractDungeon.gridSelectScreen.open(temp, Reshaper.this.magicNumber, true, cardStrings.EXTENDED_DESCRIPTION[0]);
                }

                if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {

                    for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards)
                        ModHelper.loadACard(c, p.discardPile);

                    AbstractDungeon.gridSelectScreen.selectedCards.clear();

                    this.isDone = true;

                }
                tickDuration();
            }
        });

        addToBot(new MakeTempCardInHandAction(new Star()));
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
        return new Reshaper();
    }
}
