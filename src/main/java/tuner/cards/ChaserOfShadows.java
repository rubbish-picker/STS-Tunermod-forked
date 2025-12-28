package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.GainRushPowerAction;
import tuner.cards.colorless.Star;
import tuner.interfaces.FullArtSubscriber;
import tuner.interfaces.IntoDrawPileSubscriber;

public class ChaserOfShadows extends MouldCard implements IntoDrawPileSubscriber, FullArtSubscriber {
    public ChaserOfShadows() {
        super(ChaserOfShadows.class.getSimpleName(), -2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 2;
        this.cardsToPreview = new Star();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void intoDrawPile() {
        addToBot(new MakeTempCardInHandAction(new Star(), 2));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ChaserOfShadows.this.baseMagicNumber -= 1;
                if (ChaserOfShadows.this.baseMagicNumber <= 0 && AbstractDungeon.player.drawPile.contains(ChaserOfShadows.this)) {
                    AbstractDungeon.player.drawPile.removeCard(ChaserOfShadows.this);
                    AbstractDungeon.player.drawPile.moveToExhaustPile(ChaserOfShadows.this);
                    ChaserOfShadows.this.lighten(false);
                    ChaserOfShadows.this.applyPowers();
                    AbstractDungeon.player.hand.refreshHandLayout();
                }
                this.isDone = true;
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
        return new ChaserOfShadows();
    }
}
