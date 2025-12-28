package tuner.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.red.IronWave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import tuner.interfaces.FullArtSubscriber;
import tuner.modCore.AstrographTuner;
import tuner.powers.RushPower;

import java.util.ArrayList;

public class Mapping extends MouldCard implements FullArtSubscriber {
    public static final int qianghuaTurn = 2;

    public Mapping() {
        super(Mapping.class.getSimpleName(), -2, CardType.ATTACK, CardRarity.RARE, CardTarget.NONE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    public void atBattleStart(CardGroup group) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.type == CardType.ATTACK && c.cost>0 && !(c instanceof Mapping)) {
                list.add(c);
            }
        }

        if (!list.isEmpty()) {
            AbstractCard mapcard = list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1)).makeCopy();

            AstrographTuner.qianghua.merge(mapcard.cardID, 1, Integer::sum);

            list = (ArrayList<AbstractCard>) group.group.clone();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).uuid == this.uuid) {
                    AbstractCard c = mapcard.makeCopy();

                    if (this.upgraded && c.cost > 0)
                        c.freeToPlayOnce = true;

                    group.group.set(i, c);
                    break;
                }
            }
        }
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Mapping();
    }
}
