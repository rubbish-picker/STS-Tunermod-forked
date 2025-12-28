package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.PlayTopCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CleaveEffect;
import tuner.action.PlayACardAction;
import tuner.effects.GunFireEffect;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;
import tuner.interfaces.RemoveFromDrawPileExceptDrawnSubscriber;
import tuner.powers.RushPower;

public class MoonGodSArrow extends MouldCard implements RemoveFromDrawPileExceptDrawnSubscriber, FullArtSubscriber {

    private CardGroup InWhichGroup = new CardGroup(CardGroup.CardGroupType.HAND);

    public MoonGodSArrow() {
        super(MoonGodSArrow.class.getSimpleName(), 2, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.damage = this.baseDamage = 7;
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardCrawlGame.sound.play("tunerShoot5s");
                this.isDone = true;
            }
        });
        addToBot(new VFXAction(new GunFireEffect(p.flipHorizontal)));
        addToBot(new DamageAllEnemiesAction(p, this.baseDamage,
                DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));

        if (InWhichGroup.type == CardGroup.CardGroupType.HAND) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    CardCrawlGame.sound.play("tunerShoot5s");
                    this.isDone = true;
                }
            });
            addToBot(new VFXAction(new GunFireEffect(p.flipHorizontal)));
            addToBot(new DamageAllEnemiesAction(p, this.baseDamage,
                    DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
        }
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            if (AbstractDungeon.player.hand.contains(this))
                InWhichGroup = AbstractDungeon.player.hand;
            else if (AbstractDungeon.player.discardPile.contains(this))
                InWhichGroup = AbstractDungeon.player.discardPile;
            else if (AbstractDungeon.player.drawPile.contains(this))
                InWhichGroup = AbstractDungeon.player.drawPile;
            else if (AbstractDungeon.player.exhaustPile.contains(this))
                InWhichGroup = AbstractDungeon.player.exhaustPile;
        }
    }

    @Override
    public void removeFromDrawPileExceptDrawn() {

        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractDungeon.player.hand.contains(MoonGodSArrow.this))
                    InWhichGroup = AbstractDungeon.player.hand;
                else if (AbstractDungeon.player.discardPile.contains(MoonGodSArrow.this))
                    InWhichGroup = AbstractDungeon.player.discardPile;
                else if (AbstractDungeon.player.drawPile.contains(MoonGodSArrow.this))
                    InWhichGroup = AbstractDungeon.player.drawPile;
                else if (AbstractDungeon.player.exhaustPile.contains(MoonGodSArrow.this))
                    InWhichGroup = AbstractDungeon.player.exhaustPile;

                addToTop(new PlayACardAction(MoonGodSArrow.this, InWhichGroup, null, InWhichGroup == AbstractDungeon.player.exhaustPile));
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
        return new MoonGodSArrow();
    }
}
