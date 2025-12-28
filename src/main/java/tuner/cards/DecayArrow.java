package tuner.cards;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.effects.DirectShootingEffect;
import tuner.interfaces.FullArtSubscriber;
import tuner.powers.RushPower;

public class DecayArrow extends MouldCard implements FullArtSubscriber {

    private float time = 3F;
    private int flashCount = 3;

    public DecayArrow() {
        super(DecayArrow.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = 3;
        this.damage = this.baseDamage = 16;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new DirectShootingEffect(m.hb.cX, m.hb.cY)));
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.POISON));
    }


    @Override
    public void update() {
        super.update();
        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            if (!AbstractDungeon.player.hand.contains(this)) {
                time = this.magicNumber;
                flashCount = 3;
            } else if (time >= 0) {

                if (time > 2.1 && time < 3 && flashCount >= 3) {
                    flashCount--;
                    this.flash();
                }
                if (time > 1.1 && time < 2 && flashCount >= 2) {
                    flashCount--;
                    this.flash();
                }
                if (time > 0.1 && time < 1 && flashCount >= 1) {
                    flashCount--;
                    this.flash();
                }

                time -= Gdx.graphics.getDeltaTime();
            }
            if (time < 0 && time != -100F) {
                flashCount = 3;
                time = -100F;
                addToTop(new DiscardSpecificCardAction(this, AbstractDungeon.player.hand));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(4);
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new DecayArrow();
    }
}
