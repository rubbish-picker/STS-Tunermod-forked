package tuner.cardDep;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.misc.ImaginaryReward;
import tuner.modCore.CardColorEnum;

import java.util.ArrayList;

public class BothVisualPerception extends MouldCard {
    public BothVisualPerception() {
        super(BothVisualPerception.class.getSimpleName(), 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.NONE);
        this.isEthereal = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                ArrayList<AbstractCard> list = new ArrayList<>();
                for (AbstractCard c : CardLibrary.getAllCards()) {
                    if (c.color == CardColorEnum.ImaginaryColor) {
                        list.add(c);
                    }
                }
                AbstractDungeon.getCurrRoom().rewards.add(new ImaginaryReward(AbstractDungeon.cardRng.random(0, list.size() - 1)));
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isEthereal = false;
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new BothVisualPerception();
    }
}
