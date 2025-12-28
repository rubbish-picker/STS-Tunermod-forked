package tuner.cards;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.cards.colorless.Rewriting;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;
import tuner.interfaces.IntoDrawPileSubscriber;
import tuner.interfaces.RemoveFromMasterDeckSubscriber;
import tuner.powers.RushPower;
import tuner.relics.Bless;

public class HeavyBlessings extends MouldCard implements FullArtSubscriber {
    public HeavyBlessings() {
        super(HeavyBlessings.class.getSimpleName(), -2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.cardsToPreview = new Rewriting();
        this.magicNumber = this.baseMagicNumber =  4;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void onRemoveFromMasterDeck() {

        AbstractDungeon.effectsQueue.add(new AbstractGameEffect() {

            @Override
            public void update(){
                if (AbstractDungeon.player.hasRelic(Bless.ID)) {
                    AbstractDungeon.player.getRelic(Bless.ID).counter++;
                } else {
                    Bless b = new Bless();
                    b.instantObtain();
                    CardCrawlGame.metricData.addRelicObtainData(b);
                }
                AbstractDungeon.player.increaseMaxHp(magicNumber, false);
                this.isDone = true;
            }

            @Override
            public void render(SpriteBatch spriteBatch) {}

            @Override
            public void dispose() {}
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(4);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new HeavyBlessings();
    }
}
