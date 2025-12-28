package tuner.cards;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMiscAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.interfaces.FullArtSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

public class Eurora extends MouldCard implements CustomSavable<Integer> , FullArtSubscriber {

    public Eurora() {
        super(Eurora.class.getSimpleName(), 2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 0;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractDungeon.player.increaseMaxHp(magicNumber, true);

                ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();
                for (AbstractCard c : list) {
                    if (c.uuid == Eurora.this.uuid) {
                        AbstractDungeon.player.masterDeck.removeCard(c);
                        break;
                    }
                }
                this.isDone = true;
            }
        });
    }

    public void atBattleStart() {
        this.magicNumber = this.baseMagicNumber = this.baseMagicNumber + 2;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.uuid == this.uuid) {
                c.magicNumber = c.baseMagicNumber = c.baseMagicNumber + 2;
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }

    @Override
    public Integer onSave() {
        return this.magicNumber;
    }

    @Override
    public void onLoad(Integer magic) {
        this.magicNumber = this.baseMagicNumber = magic;
    }


    @Override
    public AbstractCard makeCopy() {
        return new Eurora();
    }
}
