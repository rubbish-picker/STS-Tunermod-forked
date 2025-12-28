package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.EndTurnDeathPower;
import tuner.action.PlayACardAction;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Blasphemy extends MouldCard {
    public Blasphemy() {
        super(Blasphemy.class.getSimpleName(), 0, Imaginary, CardRarity.RARE, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 3;
        this.tags.add(CardTags.HEALING);
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                for (int i = 0; i < this.owner.magicNumber; i++)
                    addToBot(new PlayACardAction(card.makeStatEquivalentCopy(), null, target, true));
                addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new EndTurnDeathPower(AbstractDungeon.player)));
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blasphemy();
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
        if(ModHelper.imgUpgradeName(this)) {
            upgradeMagicNumber(1);
            ModHelper.initDes(this);
        }
    }
}
