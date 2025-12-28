package tuner.cards;

import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.RemoveAction;
import tuner.cards.colorless.Overwriting;
import tuner.cards.colorless.Rewriting;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;

import java.util.ArrayList;

import static tuner.modCore.CardTargetEnum.MapCard;

public class Writer extends MouldCard implements FullArtSubscriber {
    public Writer() {
        super(Writer.class.getSimpleName(), 2, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
        this.exhaust = true;
        MultiCardPreview.add(this, new Rewriting(), new Overwriting());
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MakeTempCardInHandAction(new Rewriting()));
        addToBot(new MakeTempCardInHandAction(new Overwriting()));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Writer();
    }
}
