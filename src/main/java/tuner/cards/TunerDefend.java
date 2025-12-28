package tuner.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import tuner.cards.imaginaryColor.Acrobatics;
import tuner.characters.Tuner;
import tuner.effects.AnimatedSlashEffect;
import tuner.effects.FullArtExchangeEffect;
import tuner.helpers.ModHelper;
import tuner.interfaces.FullArtSubscriber;
import tuner.misc.shijian.Shijian;

import java.lang.reflect.Field;

public class TunerDefend extends MouldCard {
    public TunerDefend() {
        super(TunerDefend.class.getSimpleName(), 1, CardType.SKILL, CardRarity.BASIC, CardTarget.NONE);
        this.block = this.baseBlock = 5;
        this.tags.add(AbstractCard.CardTags.STARTER_DEFEND);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, this.block));
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
        return new TunerDefend();
    }
}
