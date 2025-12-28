package tuner.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.LoadAHandAction;
import tuner.helpers.ModHelper;
import tuner.interfaces.NonDiscardableSubscriber;

public class Tracer extends MouldCard implements NonDiscardableSubscriber {
    public Tracer() {
        super(Tracer.class.getSimpleName(), 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.block = this.baseBlock = 8;
    }

    @Override
    public void onUseCard() {
//        if(this.baseMagicNumber>0){
//            Tracer.this.baseMagicNumber--;
//            ModHelper.loadACard(this, null);
//        }else{
//            AbstractDungeon.player.hand.moveToDiscardPile(this);
//        }
        ModHelper.loadACard(this, null);
        addToBot(new LoadAHandAction(1, true, true));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, this.block));
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Tracer();
    }
}
