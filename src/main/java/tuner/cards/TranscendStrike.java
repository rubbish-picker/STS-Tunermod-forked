package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.effects.DirectShootingEffect;
import tuner.helpers.ModHelper;
import tuner.powers.RushPower;
import tuner.relics.ATRelic;

import java.util.ArrayList;

public class TranscendStrike extends MouldCard {
    public TranscendStrike() {
        super(TranscendStrike.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        this.magicNumber = this.baseMagicNumber = 1;
        this.damage = this.baseDamage = 9;
        this.tags.add(AbstractCard.CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new DirectShootingEffect(m.hb.cX, m.hb.cY)));
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.NONE));

        for (int i = 0; i < this.magicNumber; i++)
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


                    if (p.hand.size() == 10) {
                        p.drawPile.moveToDiscardPile(c);
                        p.createHandIsFullDialog();
                    } else {
                        p.drawPile.moveToHand(c, AbstractDungeon.player.drawPile);
                    }
                    AbstractDungeon.gridSelectScreen.selectedCards.clear();
                    AbstractDungeon.player.hand.refreshHandLayout();

                    this.isDone = true;
                }
            });

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
        return new TranscendStrike();
    }
}
