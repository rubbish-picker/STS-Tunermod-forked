package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.misc.MapcardTarget;
import tuner.helpers.ModHelper;
import tuner.interfaces.NonDiscardableSubscriber;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Twinkle extends MouldCard implements NonDiscardableSubscriber {

    private AbstractCard tempc;
    private int index = -1;

    public Twinkle() {
        super(Twinkle.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.UNCOMMON, MapCard);
        this.damage = this.baseDamage = 8;
    }

    @Override
    public void onUseCard() {
        if (index != -1){
            ModHelper.refreshCard(this);
            if(index>AbstractDungeon.player.drawPile.size())
                index = AbstractDungeon.player.drawPile.size();
            AbstractDungeon.player.drawPile.group.add(index, Twinkle.this);
        } else{
            AbstractDungeon.player.hand.moveToDiscardPile(this);
        }
        index = -1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAllEnemiesAction(p, this.baseDamage, DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));

        index = -1;
        tempc = MapcardTarget.getTarget(this);
        if (tempc != null && !(tempc instanceof Twinkle)) {
            for (int i = 0; i < AbstractDungeon.player.drawPile.size(); i++) {
                if (AbstractDungeon.player.drawPile.group.get(i) == tempc) {
                    index = i;
                }
            }
        }

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if(index != -1){
                    ModHelper.deckMoveToHand(tempc);
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Twinkle();
    }
}
