package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RemoveAction;
import tuner.misc.MapcardTarget;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardTargetEnum.MapCard;

public class ArrowStep extends MouldCard {
    public ArrowStep() {
        super(ArrowStep.class.getSimpleName(), 1, CardType.SKILL, CardRarity.COMMON, MapCard);
        this.block = this.baseBlock = 8;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));

        AbstractCard temp = MapcardTarget.getTarget(this);

        addToBot(new RemoveAction(temp));

    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new ArrowStep();
    }
}
