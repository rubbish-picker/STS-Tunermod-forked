package tuner.patches.combat;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.cards.imaginaryColor.Leap;
import tuner.cards.imaginaryColor.Recycle;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.interfaces.IntoDrawPileSubscriber;
import tuner.interfaces.RemoveFromDrawPileExceptDrawnSubscriber;
import tuner.interfaces.RemoveFromDrawPileSubscriber;
import tuner.powers.ARPower;

import java.util.HashSet;
import java.util.Set;

public class DrawPileMonitor {
    public static Set<AbstractCard> oldList = new HashSet<>();
    public static Set<AbstractCard> newList = new HashSet<>();
    public static Set<AbstractCard> CardIsDrawn = new HashSet<>();

    public static void update() {
        oldList = newList;
        newList = new HashSet<>();
        newList.addAll(AbstractDungeon.player.drawPile.group);

        for (AbstractCard c : oldList) {
            if (!newList.contains(c)) {
                if (c instanceof RemoveFromDrawPileSubscriber) {
                    ((RemoveFromDrawPileSubscriber) c).removeFromDrawPile();
                }

                //魔铠特判
                if (AbstractDungeon.player.hasPower(ARPower.POWER_ID) && c.upgraded) {
                    AbstractDungeon.actionManager.addToTop(
                            new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player.getPower(ARPower.POWER_ID).amount));
                }


                if (CardIsDrawn.contains(c)) {
                    CardIsDrawn.remove(c);
                } else {
                    if (c instanceof RemoveFromDrawPileExceptDrawnSubscriber) {
                        ((RemoveFromDrawPileExceptDrawnSubscriber) c).removeFromDrawPileExceptDrawn();
                    }

                    //特判明里
                    for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
                        if (mod instanceof AbstractMod && ((AbstractMod) mod).owner instanceof Leap) {
                            AbstractDungeon.actionManager.addToBottom(
                                    new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, ((AbstractMod) mod).owner.magicNumber));
                        }
                    }

                    //魔铠特判
                    if (AbstractDungeon.player.hasPower(ARPower.POWER_ID)) {
                        if (c.canUpgrade())
                            c.upgrade();
                    }
                }
            }
        }

        for (AbstractCard c : newList) {
            if (!oldList.contains(c)) {
                if (c instanceof IntoDrawPileSubscriber) {
                    ((IntoDrawPileSubscriber) c).intoDrawPile();
                }

                //救护车碎片专用
                for (AbstractCardModifier mod : CardModifierManager.modifiers(c)) {
                    if (mod instanceof AbstractMod && ((AbstractMod) mod).owner instanceof Recycle) {
                        AbstractDungeon.actionManager.addToBottom(
                                new AbstractGameAction() {
                                    @Override
                                    public void update() {
                                        if (c.baseDamage > 0) c.baseDamage += ((AbstractMod) mod).owner.magicNumber;
                                        this.isDone = true;
                                    }
                                }
                        );
                    }
                }
            }

            CardIsDrawn.remove(c);
        }
    }

    @SpirePatch(clz = AbstractPlayer.class, method = "update")
    public static class updatePatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer _inst) {
            if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                    && GameActionManager.energyGainedThisCombat > 0) {
                DrawPileMonitor.update();
            }
        }
    }

}