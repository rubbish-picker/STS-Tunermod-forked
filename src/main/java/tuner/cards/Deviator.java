package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RewriteAction;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;
import tuner.relics.ATRelic;

public class Deviator extends MouldCard  implements FullArtSubscriber {

    public Deviator() {
        super(Deviator.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.BASIC, CardTarget.ENEMY);
        this.damage = this.baseDamage = 6;
        this.magicNumber = this.baseMagicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() < this.magicNumber + 1)
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    if (!ModHelper.canRewrote()) {
                        this.isDone = true;
                        return;
                    }
                    AbstractCard c;
                    if (p.drawPile.size() >= ATRelic.at.MaxCount) {
                        c = p.drawPile.group.get(p.drawPile.size() - ATRelic.at.MaxCount);
                    } else {
                        c = p.drawPile.group.get(0);
                    }
                    addToTop(new RewriteAction(c));
                    this.isDone = true;
                }
            });
    }


//    @Override
//    public void update() {
//        super.update();
//        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
//            if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() < this.magicNumber) {
//                this.target = MapCard;
//            } else {
//                this.target = CardTarget.NONE;
//            }
//        }
//    }

    @Override
    public void triggerOnGlowCheck() {
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.size() < this.magicNumber) {
            this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR;
        } else {
            this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR;
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        int count = AbstractDungeon.actionManager.cardsPlayedThisTurn.size();
        this.rawDescription = cardStrings.DESCRIPTION +
                cardStrings.EXTENDED_DESCRIPTION[0] + count + cardStrings.EXTENDED_DESCRIPTION[1];
        initializeDescription();
    }

    @Override
    public void onMoveToDiscard() {
        this.rawDescription = cardStrings.DESCRIPTION;
        initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Deviator();
    }
}
