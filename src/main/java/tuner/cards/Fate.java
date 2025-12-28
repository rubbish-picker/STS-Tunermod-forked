package tuner.cards;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;
import tuner.action.FateKillAllEffect;
import tuner.action.PlayACardAction;
import tuner.helpers.ModHelper;
import tuner.modCore.AstrographTuner;

import java.util.ArrayList;
import java.util.Collections;

public class Fate extends MouldCard {

    public static boolean triggerAni = false;

    public Fate() {
        super(Fate.class.getSimpleName(), 3, CardType.ATTACK, CardRarity.RARE, CardTarget.NONE);
        this.damage = this.baseDamage = 30;
        this.exhaust = true;
    }

    @Override
    public void atTurnStart() {
        if (AstrographTuner.TurnCounter == 1) {
            addToBot(new MakeTempCardInDrawPileAction(this.makeSameInstanceOf(), 1, true, true));
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        //连续打出8次后直接全屏秒杀并结束回合
        ArrayList<AbstractCard> list = new ArrayList<>(AbstractDungeon.actionManager.cardsPlayedThisCombat);
        Collections.reverse(list);
        int flag = 10;
        for (AbstractCard c : list) {
            if (c instanceof Fate) flag--;
            else break;
        }
        if (flag == 0) {
            addToBot(new VFXAction(new FateKillAllEffect()));
            triggerAni = true;
        } else if (flag > 0)

            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    this.target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                    if (this.target != null) {
                        Fate.this.calculateCardDamage((AbstractMonster) this.target);
                        this.addToTop(new DamageAction(this.target, new DamageInfo(AbstractDungeon.player, Fate.this.damage, Fate.this.damageTypeForTurn), AttackEffect.NONE));
                        this.addToTop(new VFXAction(new WeightyImpactEffect(this.target.hb.cX, this.target.hb.cY, Color.PURPLE), 0.8F));
                    }
                    this.isDone = true;
                }
            });
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust = false;
            this.rawDescription = DESCRIPTION_UPG;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Fate();
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "update")
    public static class updatePatch {

        public static int count = -1;

        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer _inst) {
            if (!AbstractDungeon.actionManager.turnHasEnded) {
                if (count < 2 && count >= 0) {
                    count = 0;
                    for (AbstractCard c : ModHelper.rtATgroup()) {
                        if (c instanceof Fate) {
                            count++;
                        }
                    }
                }

                if (!triggerAni && count >= 2) {
                    int temp = count - count % 2;
                    count = -1;
                    for (AbstractCard c : ModHelper.rtATgroup()) {
                        if (c instanceof Fate && temp > 0) {
                            AbstractDungeon.actionManager.addToBottom(
                                    new PlayACardAction(c, AbstractDungeon.player.drawPile,
                                            null, false));
                            temp--;
                        }
                    }
                    AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                        @Override
                        public void update() {
                            count = 0;
                            this.isDone = true;
                        }
                    });
                }
            }
        }
    }

}
