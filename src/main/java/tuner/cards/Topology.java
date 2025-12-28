package tuner.cards;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.colorless.Rewriting;
import tuner.interfaces.FullArtSubscriber;
import tuner.interfaces.OnObtainSubscriber;
import tuner.relics.Bless;

import java.util.ArrayList;

public class Topology extends MouldCard implements OnObtainSubscriber, CustomSavable<Integer>, FullArtSubscriber {
    public Topology() {
        super(Topology.class.getSimpleName(), 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.block = this.baseBlock = 13;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, this.block));
    }

    @Override
    public void onObtain() {

        ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();

        for (AbstractCard c : list) {
            if (c instanceof Topology && c.uuid != this.uuid) {
                AbstractDungeon.player.masterDeck.removeCard(c);
                this.baseBlock += c.baseBlock;
            }
        }
    }

    @Override
    public Integer onSave() {
        return this.baseBlock;
    }

    @Override
    public void onLoad(Integer b) {
        this.block = this.baseBlock = b;
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(5);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Topology();
    }
}
