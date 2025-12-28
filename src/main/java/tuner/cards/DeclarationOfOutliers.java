package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import tuner.action.MakeATempImaginaryAction;
import tuner.cards.imaginaryColor.Miracle;

public class DeclarationOfOutliers extends MouldCard {
    public DeclarationOfOutliers() {
        super(DeclarationOfOutliers.class.getSimpleName(), 2, CardType.SKILL, CardRarity.RARE, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 1;
        this.exhaust = true;
        this.cardsToPreview = new Miracle();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(new Miracle(),
                        (float) (Settings.WIDTH * 0.5), (float) (Settings.HEIGHT * 0.5)));
                this.isDone = true;
            }
        });
        addToBot(new MakeATempImaginaryAction(new Miracle(), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new DeclarationOfOutliers();
    }
}
