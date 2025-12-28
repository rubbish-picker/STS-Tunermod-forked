package tuner.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.action.RewriteAction;
import tuner.misc.MapcardTarget;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Daedalus extends MouldCard implements FullArtSubscriber {
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.
            getCardStrings("tuner:Daedalus");

    public int rd = 2;

    public Daedalus() {
        super(Daedalus.class.getSimpleName(), 0, CardType.SKILL, CardRarity.COMMON, CardTarget.NONE);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (ModHelper.canRewrote() && rd != 2) {
            boolean temp = rd != 0;

            if (this.upgraded) {
                addToBot(new RewriteAction(MapcardTarget.getTarget(this), temp));
            } else {
                addToBot(new RewriteAction(p.drawPile.getTopCard(), temp));
            }
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            if (rd == 2) {
                rd = AbstractDungeon.cardRandomRng.random(1);
                updateDis(rd, upgraded);
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.target = MapCard;
//            this.target = CardTarget.ENEMY;
//            this.isTargetCardCard = true;
            updateDis(rd, upgraded);
        }
    }

    private void updateDis(int rd, boolean upgraded) {

        boolean rewrite = true;

        if (rd == 0) rewrite = true;
        else if (rd == 1) rewrite = false;
        else {
            if (upgraded) {
                this.rawDescription = DESCRIPTION_UPG;
                initializeDescription();
            } else {
                this.rawDescription = DESCRIPTION;
                initializeDescription();
            }
            return;
        }

        if (!rewrite && !upgraded) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
            initializeDescription();
        }
        if (!rewrite && upgraded) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[3];
            initializeDescription();
        }
        if (rewrite && !upgraded) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
            initializeDescription();
        }
        if (rewrite && upgraded) {
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[2];
            initializeDescription();
        }

    }

    @Override
    public AbstractCard makeCopy() {
        return new Daedalus();
    }
}
