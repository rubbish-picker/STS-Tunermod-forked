package tuner.action;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import tuner.cards.EntropyReducer;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.Anger;
import tuner.cards.imaginaryColor.Loop;
import tuner.helpers.ModHelper;
import tuner.interfaces.BeingRewroteSubscriber;
import tuner.powers.ColoredPower;
import tuner.powers.MacroscopicUniversePower;
import tuner.relics.ATRelic;

import java.util.*;
import java.util.stream.Collectors;

import static tuner.modCore.CardColorEnum.ImaginaryColor;

public class RewriteAction extends AbstractGameAction {
    public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("RewriteAction")).TEXT;
    public static boolean Rewriting = false;
    public static AbstractCard cardToRewrite = null;
    public static boolean Anti;

    public boolean HasntReturnedHand;

    private int countQinxiang;

    public RewriteAction(AbstractCard c, boolean anti ,boolean isOverwriting) {
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FAST;
        cardToRewrite = c;
        HasntReturnedHand = true;
        Rewriting = true;

        Anti = anti;
        if (AbstractDungeon.player.hasPower(ColoredPower.POWER_ID)) Anti = true;

        if (cardToRewrite == null) {
            if (ModHelper.canRewrote())
                cardToRewrite = ModHelper.getRamdomTarget();
            else
                this.isDone = true;
        }

        //特判芹香
        this.countQinxiang = 0;
        for (AbstractCard ccc : ATRelic.at.dorlach.dorlachGroup.group) {
            if (ccc instanceof Anger) this.countQinxiang += ccc.baseMagicNumber;
        }
        if(isOverwriting) this.countQinxiang +=100;
    }

    public RewriteAction(AbstractCard c) {
        this(c, false, false);
    }

    public RewriteAction(AbstractCard c, boolean anti) {
        this(c, anti, false);
    }

    public void update() {
        if (this.duration == Settings.ACTION_DUR_FAST) {
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            int size = ATRelic.at.dorlach.dorlachGroup.size();

            if (!ModHelper.canRewrote() && !(cardToRewrite instanceof EntropyReducer)) {
                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, TEXT[9], true));
                this.isDone = true;
                Rewriting = false;
                return;
            }

            if (size == 0 || (cardToRewrite.cost < 0 && !Anti)) {
                tickDuration();
                return;
            }

            if (Anti) {
                AbstractDungeon.gridSelectScreen.open(rtAntiGroup(size), 1, true, TEXT[1]);
            } else {

                CardGroup temp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (AbstractCard c : ATRelic.at.dorlach.dorlachGroup.group) {
                    if (c.cost <= cardToRewrite.costForTurn && c.cost >= 0)
                        temp.addToBottom(c);
                }
                temp.sortByCost(true);
                if (!temp.isEmpty()) {
                    AbstractDungeon.gridSelectScreen.open(temp, 99, true, TEXT[0]);
                }
            }
            ATRelic.at.dorlach.dorlachGroup.glowCheck();
            ATRelic.at.dorlach.dorlachGroup.applyPowers();
            tickDuration();
        } else {
            Rewriting = false;
            if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                if (Anti) {

                    //开始修正
                    AbstractDungeon.actionManager.addToTop(new ExhaustSpecificCardAction(cardToRewrite, AbstractDungeon.player.drawPile));

                    AbstractCard card = AbstractDungeon.gridSelectScreen.selectedCards.get(0);

                    if (AbstractDungeon.player.hand.size() == 10) {
                        AbstractDungeon.getCurrRoom().souls.discard(card);
                        AbstractDungeon.player.onCardDrawOrDiscard();
                        AbstractDungeon.player.createHandIsFullDialog();
                    } else {
                        card.unhover();
                        card.lighten(true);
                        card.setAngle(0.0F);
                        card.drawScale = 0.12F;
                        card.targetDrawScale = 0.75F;
                        card.current_x = CardGroup.DRAW_PILE_X;
                        card.current_y = CardGroup.DRAW_PILE_Y;
                        card.flash();
                        AbstractDungeon.player.hand.addToTop(card);
                        AbstractDungeon.player.hand.refreshHandLayout();
                        AbstractDungeon.player.hand.applyPowers();
                    }

                    this.isDone = true;
                } else {
                    int costSum = 0;
                    for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                        costSum += c.cost;
                    }

                    //开始安定
                    if (costSum <= cardToRewrite.costForTurn) {

                        //特判dead card
                        int flag = 1;
                        if (AbstractDungeon.player.hasPower(MacroscopicUniversePower.POWER_ID)) {
                            flag += AbstractDungeon.player.getPower(MacroscopicUniversePower.POWER_ID).amount;
                        }

                        for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {

                            for (int i = 0;
                                 i < flag;
                                 i++) {
                                if (((MouldCard) c).modifier != null)
                                    CardModifierManager.addModifier(cardToRewrite, (((MouldCard) c).modifier));
                                ((MouldCard) c).rewritingChange(cardToRewrite);
                            }
                            if (!(c instanceof Loop))
                                ATRelic.at.dorlach.dorlachGroup.removeCard(c);
                        }
                        ModHelper.CalculateEffect(cardToRewrite);
//                        ModHelper.ChangeRewroteCardImg(cardToRewrite);

                        ModHelper.deckMoveToHand(cardToRewrite);
                    } else {
                        AbstractDungeon.actionManager.addToTop(new RewriteAction(cardToRewrite));
                    }
                    this.isDone = true;
                }

                //安定成功后触发trigger
                if (cardToRewrite instanceof BeingRewroteSubscriber)
                    ((BeingRewroteSubscriber) cardToRewrite).beingRewrote();

                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            } else {
                //安定修正失败的触发位置
                if (HasntReturnedHand) {
                    HasntReturnedHand = false;

                    if (cardToRewrite instanceof EntropyReducer && Anti) {
                        ((EntropyReducer) cardToRewrite).noRewrote();
                    } else {
                        //c加入手牌
                        ModHelper.deckMoveToHand(cardToRewrite);
                        cardToRewrite.flash();
                    }
                }
            }
            tickDuration();
        }
    }

    private CardGroup rtAntiGroup(int num) {
        CardGroup derp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        //30%生成色图;不是色牌时，45%出普通，15%出稀有
        //40%升级
        int DLC = 30, ROLL1 = 45, ROLL2 = 85, UPGRADE = 30;
        AbstractCard.CardColor dlcColor = ModHelper.rtDLCColor();

        while (derp.size() != num) {
            boolean dupe = false;
            int roll = ModHelper.selfRamdom.random(99);
            int isDLC = ModHelper.selfRamdom.random(99);
            if (!ModHelper.isSetuAvail()) isDLC = 100;

            AbstractCard.CardRarity cardRarity;
            if (roll < ROLL1) {
                cardRarity = AbstractCard.CardRarity.COMMON;
            } else if (roll < ROLL2) {
                cardRarity = AbstractCard.CardRarity.UNCOMMON;
            } else {
                cardRarity = AbstractCard.CardRarity.RARE;
            }

            int finalIsDLC = isDLC;
            List<AbstractCard> g = CardLibrary.getAllCards().stream()
                    .filter(item -> {
                        if (finalIsDLC > DLC)
                            return item.color != ImaginaryColor && item.color != dlcColor && item.rarity == cardRarity && !item.hasTag(AbstractCard.CardTags.HEALING);
                        else return item.color == dlcColor;
                    })
                    .collect(Collectors.toList());
            AbstractCard tmp = g.get(ModHelper.selfRamdom.random(g.size() - 1)).makeCopy();

            if (isDLC > DLC) {
                if (ModHelper.selfRamdom.random(99) < (UPGRADE + this.countQinxiang) || AbstractDungeon.player.hasPower("MasterRealityPower"))
                    tmp.upgrade();
            }

            Iterator var6 = derp.group.iterator();

            while (var6.hasNext()) {
                AbstractCard c = (AbstractCard) var6.next();
                if (c.cardID.equals(tmp.cardID)) {
                    dupe = true;
                    break;
                }
            }

            if (!dupe) {
                derp.group.add(tmp);
            }
        }

        return derp;
    }

}
