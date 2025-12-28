package tuner.action;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import tuner.cards.MelodyOfTheFinale;
import tuner.effects.FlyingBigDaggerEffect;
import tuner.helpers.ModHelper;

public class MelodyOfTheFinaleAction extends AbstractGameAction {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("tuner:MelodyOfTheFinaleAction")).TEXT;

    private int count;
    private int magicNumber;
    private int damage;

    public MelodyOfTheFinaleAction(int count, int magicNumber, int damage) {
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.count = count;
        this.magicNumber = magicNumber;
        this.damage = damage;
    }

    @Override
    public void update() {
        if (!ModHelper.canRewrote()) {
            this.isDone = true;
            return;
        }

        if (this.duration == Settings.ACTION_DUR_FAST) {
            CardGroup temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : ModHelper.rtATgroup()) {
                temp.addToTop(c);
            }
            AbstractDungeon.gridSelectScreen.open(temp, 99, true, TEXT[0]);
        }

        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                //丢弃
                AbstractDungeon.player.drawPile.moveToDiscardPile(c);
                GameActionManager.incrementDiscard(false);
                c.triggerOnManualDiscard();
            }

            if (AbstractDungeon.gridSelectScreen.selectedCards.size() >= this.magicNumber && count > 0) {
                addToTop(new MelodyOfTheFinaleAction(this.count-1, this.magicNumber, this.damage));
                addToTop(new WaitAction(0.1F));
                addToTop(new WaitAction(0.1F));
                addToTop(new DamageAllEnemiesAction(AbstractDungeon.player, this.damage,
                        DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
                addToTop(new VFXAction(new FlyingBigDaggerEffect(
                        AbstractDungeon.player.hb.cX + 100 * Settings.scale,
                        AbstractDungeon.player.hb.cY - 100 * Settings.scale,
                        AbstractDungeon.player.flipHorizontal)));
            }

            AbstractDungeon.player.hand.refreshHandLayout();
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            this.isDone = true;

        }

        tickDuration();
    }
}
