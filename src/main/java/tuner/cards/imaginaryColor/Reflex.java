package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.defect.AllCostToHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.NecronomicurseEffect;
import tuner.action.MakeATempImaginaryAction;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;
import tuner.interfaces.OnObtainSubscriber;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Reflex extends MouldCard implements OnObtainSubscriber {

    private boolean shouldCopy;

    public Reflex() {
        super(Reflex.class.getSimpleName(), -2, Imaginary, CardRarity.UNCOMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 2;
        this.tags.add(CardTags.HEALING);
        this.shouldCopy = true;
        this.modifier = new AbstractMod(this) {
        };
    }

    public Reflex(boolean shouldCopy) {
        this();
        this.shouldCopy = false;
    }

    @Override
    public AbstractCard makeCopy() {
        return new Reflex();
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
    }

    @Override
    public void onObtain() {
        if (shouldCopy) {
            AbstractDungeon.effectsQueue.add(new NecronomicurseEffect(new Reflex(false), (float) Settings.WIDTH / 2.0F - AbstractCard.IMG_WIDTH_S * 0.5F, (float) Settings.HEIGHT / 2.0F));
            AbstractDungeon.effectsQueue.add(new NecronomicurseEffect(new Reflex(false), (float) Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH_S * 0.5F, (float) Settings.HEIGHT / 2.0F));
        }
    }
}
