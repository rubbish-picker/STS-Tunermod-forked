package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.action.MelodyOfTheFinaleAction;
import tuner.action.PlayACardAction;
import tuner.effects.FlyingBigDaggerEffect;
import tuner.effects.GunFireEffect;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;
import tuner.interfaces.RemoveFromDrawPileExceptDrawnSubscriber;

public class MelodyOfTheFinale extends MouldCard implements FullArtSubscriber {

    public MelodyOfTheFinale() {
        super(MelodyOfTheFinale.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.damage = this.baseDamage = 7;
        this.isMultiDamage = true;
        this.magicNumber = this.baseMagicNumber = 4;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new FlyingBigDaggerEffect(
                p.hb.cX + 100 * Settings.scale, p.hb.cY - 100 * Settings.scale, p.flipHorizontal)));
        addToBot(new DamageAllEnemiesAction(p, this.baseDamage,
                DamageInfo.DamageType.NORMAL, AbstractGameAction.AttackEffect.NONE));
        addToBot(new WaitAction(0.1F));
        addToBot(new WaitAction(0.1F));
        addToBot(new MelodyOfTheFinaleAction(3, this.magicNumber, this.baseDamage));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(-1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new MelodyOfTheFinale();
    }
}
