package tuner.patches.tuner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;
import tuner.cards.MouldCard;
import tuner.relics.ATRelic;

import static tuner.modCore.CardTargetEnum.MapCard;

public class PatchSingleTargetInput {

    public static Mapcard mapcard;

    public static AbstractMonster M = new Cultist(0, 0);

    @SpirePatch(clz = AbstractPlayer.class, method = "renderTargetingUi")
    public static class RenderColorPatch {
        private static final Color PURPLE = new Color(1.0F, 0.0F, 1.0F, 0.8F);

        @SpireInsertPatch(rloc = 24)
        public static SpireReturn Insert(AbstractPlayer _inst, SpriteBatch sb) {
            if (_inst.hoveredCard instanceof MouldCard && _inst.hoveredCard.target == MapCard && ATRelic.at.hoveredMapcard != null) {
                sb.setColor(PURPLE);
            }
            return SpireReturn.Continue();
        }
    }

//    @SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
//    public static class SelectPatch {
//
//        public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("tuner:NoTargetCard")).TEXT;
//
//        private static void biiiiiiiig(AbstractPlayer p) {
//            ModHelper.logger.info("=================================");
//            if (p.hoveredCard != null) {
//                ModHelper.logger.info("=================当前打出中的手牌为:" + p.hoveredCard + "==============");
//            } else
//                ModHelper.logger.info("=================当前没有被选中的手牌==============");
//            if (mapcard != null) {
//                ModHelper.logger.info("=================当前mapcard.c为:" + mapcard.c + "==============");
//            } else
//                ModHelper.logger.info("=================当前mapcard.c == null==============");
//            if (ATRelic.at.hoveredMapcard != null) {
//                ModHelper.logger.info("=================当前ATRelic.at.hoveredMapcard.c:" + ATRelic.at.hoveredMapcard.c + "==============");
//            } else
//                ModHelper.logger.info("=================当前ATRelic.at.hoveredMapcard.c == null==============");
//            ModHelper.logger.info("=================================");
//        }
//
//        private static boolean queueContains(AbstractCard card) {
//            for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
//                if (i.card == card)
//                    return true;
//            }
//            return false;
//        }
//
//        private static void setUsingClickDragControl(AbstractPlayer p, boolean b) {
//            try {
//                Field field = AbstractPlayer.class.getDeclaredField("isUsingClickDragControl");
//                field.setAccessible(true);
//                field.set(p, b);
//            } catch (Exception e) {
//                ModHelper.logger.info(e);
//                e.printStackTrace();
//            }
//        }
//
//        @SpirePrefixPatch
//        public static SpireReturn prefix(AbstractPlayer _inst, float ___hoverStartLine, boolean ___isUsingClickDragControl) {
//            if (_inst.hoveredCard instanceof MouldCard && ((MouldCard) _inst.hoveredCard).isTargetCardCard) {
//                AbstractCard cardFromHotkey;
//
//                //选择指向的牌
//                if (ModHelper.canRewrote() && ATRelic.at.hoveredMapcard != null)
//                    mapcard = ATRelic.at.hoveredMapcard;
//                else mapcard = null;
//
//                if (!AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead() &&
//                        !InputHelper.justClickedRight &&
//                        !((float) InputHelper.mY < 50.0F * Settings.scale) &&
//                        !((float) InputHelper.mY < ___hoverStartLine - 400.0F * Settings.scale)) {
//
//                    cardFromHotkey = InputHelper.getCardSelectedByHotkey(_inst.hand);
//                    if (cardFromHotkey != null && !queueContains(cardFromHotkey)) {
//                        boolean isSameCard = cardFromHotkey == _inst.hoveredCard;
//                        _inst.releaseCard();
//                        if (isSameCard) {
//                            GameCursor.hidden = false;
//                        } else {
//                            _inst.hoveredCard = cardFromHotkey;
//                            _inst.hoveredCard.setAngle(0.0F, false);
//                            setUsingClickDragControl(_inst, true);
//                            _inst.isDraggingCard = true;
//                        }
//                    }
//
//                    if (!InputHelper.justClickedLeft && !InputActionSet.confirm.isJustPressed() && !CInputActionSet.select.isJustPressed()) {
//                        if (!___isUsingClickDragControl && InputHelper.justReleasedClickLeft && mapcard != null) {
//                            if (_inst.hoveredCard.canUse(_inst, M)) {
//                                ((MouldCard) _inst.hoveredCard).targetCard = mapcard.c;
//                                InputHelper.justClickedLeft = false;
//                                _inst.hoverEnemyWaitTimer = 1.0F;
//                                _inst.hoveredCard.unhover();
//
//                                if (!queueContains(_inst.hoveredCard))
//                                    AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(_inst.hoveredCard, PatchSingleTargetInput.M));
//
////                            注意原版这里有null，但是我加了闪退
////                                _inst.hoveredCard = null;
//
//                                _inst.isDraggingCard = false;
//
//                            } else {
//                                biiiiiiiig(_inst);
//                                AbstractDungeon.effectList.add(new ThoughtBubble(_inst.dialogX, _inst.dialogY, 3.0F, TEXT[0], true));
//                                _inst.releaseCard();
//                            }
//
//
//                            _inst.inSingleTargetMode = false;
//                            GameCursor.hidden = false;
//                        }
//                    } else {
//                        InputHelper.justClickedLeft = false;
//                        if (mapcard != null && _inst.hoveredCard.canUse(_inst, M)) {
//                            ((MouldCard) _inst.hoveredCard).targetCard = mapcard.c;
//                            _inst.hoverEnemyWaitTimer = 1.0F;
//                            _inst.hoveredCard.unhover();
//
//                            if (!queueContains(_inst.hoveredCard))
//                                AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(_inst.hoveredCard, PatchSingleTargetInput.M));
//
////                            注意原版这里有null，但是我加了闪退
////                            _inst.hoveredCard = null;
//
//                            _inst.isDraggingCard = false;
//
//                        } else {
//                            AbstractDungeon.effectList.add(new ThoughtBubble(_inst.dialogX, _inst.dialogY, 3.0F, TEXT[0], true));
//                            biiiiiiiig(_inst);
//                            _inst.releaseCard();
//                        }
//
//
//                        _inst.inSingleTargetMode = false;
//                        GameCursor.hidden = false;
//
//                    }
//                } else {
//                    if (Settings.isTouchScreen) {
//                        InputHelper.moveCursorToNeutralPosition();
//                    }
//
//                    _inst.releaseCard();
//                    CardCrawlGame.sound.play("UI_CLICK_2");
//                    setUsingClickDragControl(_inst, false);
//                    _inst.inSingleTargetMode = false;
//                    GameCursor.hidden = false;
//                }
//                return SpireReturn.Return();
//            }
//            return SpireReturn.Continue();
//        }
//    }


//    @SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
//    public static class SelectPatch {
//        @SpireInsertPatch(rloc = 17)
//        public static void Insert(AbstractPlayer _inst) {
//            if (_inst.hoveredCard instanceof MouldCard) {
//                //重置上一次的选择
//                ((MouldCard) _inst.hoveredCard).targetCard = null;
//                boolean isHovered = false;
//                if (ModHelper.canRewrote())
//                    for (AT.Mapcard m : ATRelic.at.oldList) {
//                        m.hb.update();
//                        if (m.hb.hovered) {
//                            mapcard = m;
//                            isHovered = true;
//                            break;
//                        }
//                    }
//                if (!isHovered) {
//                    mapcard = null;
//                }
//            }
//        }
//    }
//
//
//    @SpirePatch(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
//    public static class updateSingleTargetInputPatch {
//        public static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("tuner:NoTargetCard")).TEXT;
//
//        @SpireInsertPatch(rloc = 66)
//        public static SpireReturn Insert1(AbstractPlayer _inst) {
//            return getSpireReturn(_inst);
//        }
//
//        @SpireInsertPatch(rloc = 89)
//        public static SpireReturn Insert2(AbstractPlayer _inst) {
//            return getSpireReturn(_inst);
//        }
//
//        private static SpireReturn getSpireReturn(AbstractPlayer _inst) {
//            if (_inst.hoveredCard instanceof MouldCard && ((MouldCard) _inst.hoveredCard).isTargetCardCard) {
//                if (mapcard != null) {
//                    ((MouldCard) _inst.hoveredCard).targetCard = mapcard.c;
//                    InputHelper.justClickedLeft = false;
//                    _inst.hoverEnemyWaitTimer = 1.0F;
//                    _inst.hoveredCard.unhover();
//
//                    if (!queueContains(_inst.hoveredCard))
//                        AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(_inst.hoveredCard, PatchSingleTargetInput.M));
//                    _inst.hoveredCard = null;
//                    _inst.isDraggingCard = false;
//
//                } else {
//                    AbstractDungeon.effectList.add(new ThoughtBubble(_inst.dialogX, _inst.dialogY, 3.0F, TEXT[0], true));
//                    _inst.releaseCard();
//                }
//
//
//                try {
//                    Field field = AbstractPlayer.class.getDeclaredField("isUsingClickDragControl");
//                    field.setAccessible(true);
//                    field.set(_inst, false);
//                } catch (Exception e) {
//                    ModHelper.logger.info("============SingleTargetInputBig!!!==========");
//                    ModHelper.logger.info(e);
//                    ModHelper.logger.info("======================BIGEND=================");
//                    e.printStackTrace();
//                }
//
//                _inst.inSingleTargetMode = false;
//                GameCursor.hidden = false;
//                return SpireReturn.Return();
//            }
//            return SpireReturn.Continue();
//        }
//
//        private static boolean queueContains(AbstractCard card) {
//            for (CardQueueItem i : AbstractDungeon.actionManager.cardQueue) {
//                if (i.card == card)
//                    return true;
//            }
//            return false;
//        }
//
//    }

}
