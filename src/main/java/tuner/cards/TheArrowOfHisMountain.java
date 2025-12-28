package tuner.cards;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Overclock;
import com.megacrit.cardcrawl.cards.green.Prepared;
import com.megacrit.cardcrawl.cards.purple.ThirdEye;
import com.megacrit.cardcrawl.cards.red.Warcry;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import tuner.interfaces.OnObtainSubscriber;

import java.util.ArrayList;

public class TheArrowOfHisMountain extends MouldCard implements OnObtainSubscriber {


    public TheArrowOfHisMountain() {
        super(TheArrowOfHisMountain.class.getSimpleName(), -2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.tags.add(CardTags.HEALING);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public void onObtain() {
        AbstractDungeon.effectsQueue.add(new AbstractGameEffect() {
            private boolean once = true;

            @Override
            public void update() {
                if (once) {
                    once = false;

                    ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();
                    for (AbstractCard c : list) {
                        if (c instanceof TheArrowOfHisMountain) {
                            AbstractDungeon.player.masterDeck.group.remove(c);
                        }
                    }

                    list = new ArrayList<>();
                    AbstractCard w = new Warcry();
                    w.upgrade();
                    list.add(w);

                    AbstractCard o = new Overclock();
                    o.upgrade();
                    list.add(o);

                    AbstractCard p = new Prepared();
                    p.upgrade();
                    list.add(p);

                    AbstractCard t = new ThirdEye();
                    t.upgrade();
                    list.add(t);

                    AbstractDungeon.cardRewardScreen.customCombatOpen(list, CardRewardScreen.TEXT[1], false);
                }
                if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
                    AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(
                            AbstractDungeon.cardRewardScreen.discoveryCard,
                            Settings.WIDTH * 0.4F,
                            Settings.HEIGHT * 0.5F));
                    this.isDone = true;
                }
            }

            @Override
            public void render(SpriteBatch spriteBatch) {

            }

            @Override
            public void dispose() {

            }
        });
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new TheArrowOfHisMountain();
    }
}
