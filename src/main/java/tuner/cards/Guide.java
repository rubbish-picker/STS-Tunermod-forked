package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.LoadAHandAction;
import tuner.helpers.ModHelper;
import tuner.relics.ATRelic;

public class Guide extends MouldCard {
    public Guide() {
        super(Guide.class.getSimpleName(), 0, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.NONE);
        this.magicNumber = this.baseMagicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new LoadAHandAction(1));
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (ModHelper.canRewrote()) {
                    int flag = -1000;
                    boolean avail = true;
                    for (AbstractCard c : ModHelper.rtATgroup()) {
                        if (c.costForTurn >= flag) {
                            flag = c.costForTurn;
                        } else {
                            avail = false;
                        }
                    }

                    if (avail)
                        addToTop(new GainEnergyAction(magicNumber));
                    else {
                        avail = true;
                        flag = 1000;
                        for (AbstractCard c : ModHelper.rtATgroup()) {
                            if (c.costForTurn <= flag) {
                                flag = c.costForTurn;
                            } else {
                                avail = false;
                            }
                        }

                        if (avail)
                            addToTop(new GainEnergyAction(magicNumber));

                    }
                }
                this.isDone = true;
            }
        });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Guide();
    }
}
