package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import tuner.action.LoadAHandAction;
import tuner.action.RewriteAction;
import tuner.helpers.ModHelper;
import tuner.powers.RushPower;
import tuner.relics.ATRelic;

public class StrikeHard extends MouldCard {
    public StrikeHard() {
        super(StrikeHard.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 9;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));

        if(this.upgraded) {
            addToBot(new LoadAHandAction(1, true, true));
        }

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

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            this.rawDescription = DESCRIPTION_UPG;
            this.initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new StrikeHard();
    }
}
