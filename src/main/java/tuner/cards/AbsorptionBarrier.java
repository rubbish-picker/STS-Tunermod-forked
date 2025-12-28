package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class AbsorptionBarrier extends MouldCard {

    public boolean onUse = false;
    private boolean removeBuffLock = false;

    public AbsorptionBarrier() {
        super(AbsorptionBarrier.class.getSimpleName(), 2, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
        this.block = this.baseBlock = 10;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbsorptionBarrier.this.onUse = true;
        addToBot(new GainBlockAction(p, this.block));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbsorptionBarrier.this.onUse = false;
                this.isDone = true;
            }
        });
    }

    //patch写到幽化随想的power里去了（别骂我
    public void onGainBlock(int amt) {
        this.baseBlock += amt;
        this.applyPowers();
    }

    private void removeFrail() {
        removeBuffLock = true;
        addToBot(new RemoveSpecificPowerAction(
                AbstractDungeon.player, AbstractDungeon.player, "Frail"));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                removeBuffLock = false;
                this.isDone = true;
            }
        });
    }

    @Override
    public void update() {
        if (AbstractDungeon.currMapNode != null
                && AbstractDungeon.player != null
                && (AbstractDungeon.getCurrRoom()).phase == AbstractRoom.RoomPhase.COMBAT
                && AbstractDungeon.player.hand != null) {
            if (this.upgraded && AbstractDungeon.player.hasPower("Frail") && !removeBuffLock) {
                removeFrail();
            }
        }
        super.update();
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
        return new AbsorptionBarrier();
    }
}
