package tuner.cards.imaginaryColor;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;
import tuner.powers.SleepPower;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Sleep extends MouldCard {
    public Sleep() {
        super(Sleep.class.getSimpleName(), 3, Imaginary, CardRarity.RARE, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 1;
        this.exhaust = true;
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                target = ModHelper.rtTarget(target);
                if (target != null) {
                    AbstractCreature m = target;
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            addToBot(new ApplyPowerAction(m, AbstractDungeon.player, new SleepPower(m, owner.baseMagicNumber)));
                            this.isDone = true;
                        }
                    });
                }
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Sleep();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
        if (ModHelper.imgUpgradeName(this)) {
            if (this.timesUpgraded == 1) {
                this.exhaust = false;
                this.rawDescription = DESCRIPTION_UPG;
                this.initializeDescription();
            }
            if (this.timesUpgraded == 2) {
                upgradeBaseCost(2);
            }
        }
    }
}
