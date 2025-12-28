package tuner.patches.tuner;

import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.CardGlowBorder;
import tuner.action.PlayACardAction;
import tuner.cards.AR;
import tuner.cards.TargetedElimination;
import tuner.helpers.ModHelper;
import tuner.helpers.MyImageMaster;
import tuner.interfaces.OnRightClickSubscriber;
import tuner.modCore.CardTargetEnum;
import tuner.powers.TuningFormPower3;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.isScreenUp;
import static com.megacrit.cardcrawl.helpers.MathHelper.cardScaleLerpSnap;

public class AT {
    //华丽地渲染抽牌堆上方若干张牌
    static final float AniTime = .5F;
    private static final Color color = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private static final Color PURPLE = new Color(1.0F, 0.0F, 1.0F, 0.8F);
    private static final TextureAtlas.AtlasRegion flight = AbstractPower.atlas.findRegion("128/flight");
    private static final TextureAtlas.AtlasRegion pin = new TextureAtlas.AtlasRegion(MyImageMaster.pin, 0, 0, MyImageMaster.pin.getWidth(), MyImageMaster.pin.getHeight());
    static final float IMG_WIDTH = 300.0F * Settings.scale;
    static final float IMG_HEIGHT = 420.0F * Settings.scale;

    static float drawStartX;
    private static float drawEndX;
    private static float drawStartY;
    private static float padX;
    private static float SCALE;

    private static float targetUIx, targetUIy;

//    public static HashMap<String, AbstractCard> ALL_CARDS_FOR_RENDER_TIP = new HashMap<>();
    private static final Map<String, AbstractCard> ALL_CARDS_FOR_RENDER_TIP =
            Collections.synchronizedMap(new WeakHashMap<>());

    public int MaxCount;
    public Set<Mapcard> oldList;
    public Set<Mapcard> newList;
    public Dorlach dorlach;

    public boolean hide;

    //这个状态下，不会修改卡牌显示的尺寸
    public boolean onCloseAni;

    public Mapcard hoveredMapcard;
    public AbstractMonster hoveredMonster;

    public Mapcard cardInUseForTunerForm = null;
    public boolean isCardWaitingForSecondClickForTunerForm = false;
    public boolean isCardInPlayForTunerForm = false;

    public AT() {
        init();
        dorlach = new Dorlach(drawEndX + 40F * Settings.scale, drawStartY + 20F * Settings.scale);
    }

    public void init() {
        this.hide = true;
        this.onCloseAni = false;

        if (newList == null) {
            newList = new HashSet<>();
        } else
            newList.clear();
        if (oldList == null) {
            oldList = new HashSet<>();
        } else
            oldList.clear();

        MaxCount = 1;

        drawStartX = (float) Settings.WIDTH * 0.4F;
//        drawEndX = drawStartX + MaxCount * 130.0F * Settings.SCALE + 200.0F * Settings.SCALE;
        drawEndX = (float) Settings.WIDTH * 0.4F;
        drawStartY = (float) Settings.HEIGHT * 0.72F;
        padX = 160.0F * Settings.scale;

        SCALE = 5F / MaxCount;
        targetUIx = 0;
        targetUIy = 0;

    }

    public void update() {
        if (!isScreenUp) {
            this.dorlach.update();


            if (!onCloseAni)
                SCALE = (float) Math.sqrt(5F / MaxCount);

            if (SCALE > 1.5F) SCALE = 1.5F;
            drawStartX = (float) Settings.WIDTH * 0.4F - MaxCount * 80.0F * Settings.scale * SCALE;
            drawEndX = (float) Settings.WIDTH * 0.4F + MaxCount * 80.0F * Settings.scale * SCALE;
            padX = (drawEndX - drawStartX) / MaxCount;
            dorlach.setXY(drawEndX + 40F * Settings.scale, drawStartY + 20F * Settings.scale);


            //删除部分2，将部分0转化为1
            Iterator<Mapcard> it = oldList.iterator();
            Mapcard mapcard;
            while (it.hasNext()) {
                mapcard = it.next();
                if (mapcard.status == Mapcard.MapCardStatus.SUSPENDED) {
                    mapcard.disappearance -= Gdx.graphics.getDeltaTime() * 2F;
                    if (mapcard.disappearance < 0) mapcard.disappearance = 0;
                }
                if (mapcard.status == Mapcard.MapCardStatus.READY) {
                    mapcard.emergence -= Gdx.graphics.getDeltaTime() * 2F;
                    if (mapcard.emergence < 0) mapcard.emergence = 0;
                }

                if (mapcard.emergence == 0) {
                    mapcard.status = Mapcard.MapCardStatus.RUNNING;
                    mapcard.emergence = AniTime;
                }

                if (mapcard.disappearance == 0) {
                    it.remove(); //删除2
                }
            }

            //根据现在的抽牌堆创建新的映射牌表
            newList = new HashSet<>();
            AbstractCard tempCard;
            for (int i = AbstractDungeon.player.drawPile.size() - 1;
                 i >= AbstractDungeon.player.drawPile.size() - MaxCount && i >= 0; i--) {
                tempCard = AbstractDungeon.player.drawPile.group.get(i);
                newList.add(new Mapcard(this, tempCard,
                        i - AbstractDungeon.player.drawPile.size() + MaxCount, Mapcard.MapCardStatus.NULL));
            }

            //和之前的对比，修改卡牌映射的状态,更新target
            Mapcard oldm;
            for (Mapcard m : newList) {
                oldm = contain(m.c, oldList);
                if (oldm != null) {
                    oldm.index = m.index;
                } else {
                    oldList.add(new Mapcard(this, m.c, m.index, Mapcard.MapCardStatus.READY));
                }
            }

            for (Mapcard m : oldList) {
                if (contain(m.c, newList) == null) {
                    m.status = Mapcard.MapCardStatus.SUSPENDED;
                }
            }

            //更新每张牌的位置与状态
            boolean isHoverd = false;
            for (Mapcard m : oldList) {
                selfUpdate(m.c);

                m.currentX = cardScaleLerpSnap(m.currentX, rtTargetX(m.index));
                m.hb.move(m.currentX, drawStartY);
                m.hb.resize(IMG_WIDTH * m.drawScale, IMG_HEIGHT * m.drawScale);
                m.hb.encapsulatedUpdate(m);

                if (m.hb.hovered && m.status == Mapcard.MapCardStatus.RUNNING) {
                    m.drawScale = 0.9F;
                    hoveredMapcard = m;
                    isHoverd = true;
                } else m.drawScale = 0.5F * SCALE;

                //右键适配qwq
                if (m.rightClickStarted && InputHelper.justReleasedClickRight) {
                    if (m.hb.hovered)
                        m.rightClick = true;
                    m.rightClickStarted = false;
                }
                if (m.hb != null &&
                        m.hb.hovered &&
                        InputHelper.justClickedRight)
                    m.rightClickStarted = true;
                if (m.rightClick && m.c instanceof OnRightClickSubscriber) {
                    ((OnRightClickSubscriber) m.c).onRightClick();
                    m.rightClick = false;
                }
            }

            if (!isHoverd) hoveredMapcard = null;

            //轻盈形态
            isHoverd = false;
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                m.hb.update();
                if (m.hb.hovered && !m.isDying && !m.isEscaping && m.currentHealth > 0) {
                    hoveredMonster = m;
                    isHoverd = true;
                    break;
                }
            }
            if (!isHoverd) hoveredMonster = null;

            if (this.cardInUseForTunerForm != null && !this.isCardInPlayForTunerForm) {
                this.isCardInPlayForTunerForm = true;

                AbstractCard c = this.cardInUseForTunerForm.c;
                if (c.target == AbstractCard.CardTarget.SELF_AND_ENEMY ||
                        c.target == AbstractCard.CardTarget.ENEMY ||
                        c.target == CardTargetEnum.MapCard) {
                    isCardWaitingForSecondClickForTunerForm = true;
                } else {
                    tunerFormPlayACard(c, null);
                }
            }

            if (this.cardInUseForTunerForm != null && this.isCardWaitingForSecondClickForTunerForm) {
                AbstractCard c = this.cardInUseForTunerForm.c;
                if (InputHelper.justClickedLeft) {
                    if (c.target == AbstractCard.CardTarget.SELF_AND_ENEMY ||
                            c.target == AbstractCard.CardTarget.ENEMY) {
                        if (hoveredMonster != null) {
                            tunerFormPlayACard(c, hoveredMonster);
                            isCardWaitingForSecondClickForTunerForm = false;
                        }
                    } else if (c.target == CardTargetEnum.MapCard) {
                        if (hoveredMapcard != null) {
                            tunerFormPlayACard(c, hoveredMapcard);
                            isCardWaitingForSecondClickForTunerForm = false;
                        }
                    }


                } else if (InputHelper.justClickedRight) { // 检测右键点击取消
                    CardCrawlGame.sound.play("UI_CLICK_1");
                    AT.this.cardInUseForTunerForm = null;
                    AT.this.isCardWaitingForSecondClickForTunerForm = false;
                    AT.this.isCardInPlayForTunerForm = false;
                }
            }

        }
    }


    private void renderMapcardTip(Mapcard m, SpriteBatch sb) {
        if (!isScreenUp) {
            if (ALL_CARDS_FOR_RENDER_TIP.get(m.c.cardID) == null) {
                ALL_CARDS_FOR_RENDER_TIP.put(m.c.cardID, m.c.makeCopy());
            }

            AbstractCard copy = ALL_CARDS_FOR_RENDER_TIP.get(m.c.cardID);
            copy.current_x = m.c.current_x;
            copy.current_y = m.c.current_y;
            copy.drawScale = m.c.drawScale;

            CardModifierManager.removeAllModifiers(copy, true);
            for (AbstractCardModifier mod : CardModifierManager.modifiers(m.c)) {
                CardModifierManager.addModifier(copy, mod.makeCopy());
            }
            TipHelper.renderTipForCard(copy, sb, m.c.keywords);
        }
    }

    private void setOrResetGlowList(ArrayList<CardGlowBorder> glowBorders, AbstractCard c, boolean flag) {
        try {
            Field f = AbstractCard.class.getDeclaredField("glowList");
            f.setAccessible(true);
            if (flag) {
                glowBorders.addAll((ArrayList<CardGlowBorder>) f.get(c));
                f.set(c, new ArrayList<CardGlowBorder>());
            } else {
                f.set(c, glowBorders);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isOutOfMon(AbstractCard c) {
        if (ModHelper.canRewrote()) {
            AbstractMonster monster = AbstractDungeon.getCurrRoom().monsters.hoveredMonster;
            if (monster != null) {
                return monster.drawX - AbstractCard.IMG_WIDTH_S * 0.4F < c.current_x;
            } else return false;
        }
        return false;
    }

    private void renderAMapCard(Mapcard m, SpriteBatch sb) {
        float tempCurrentX, tempCurrentY, tempTransparency, tempTargetTransparency, tempScale;
        boolean tempIsGlowing;
        ArrayList<CardGlowBorder> tempGlowBorders = new ArrayList<>();
        tempCurrentX = m.c.current_x;
        tempCurrentY = m.c.current_y;
        tempTransparency = m.c.transparency;
        tempTargetTransparency = m.c.targetTransparency;
        tempScale = m.c.drawScale;
        tempIsGlowing = m.c.isGlowing;
        setOrResetGlowList(tempGlowBorders, m.c, true);

        m.c.current_x = m.currentX;
        m.c.current_y = drawStartY;
        m.c.drawScale = m.drawScale;
//            m.c.angle -= 1;
        if (m.status == Mapcard.MapCardStatus.READY) {
            m.c.transparency = m.c.targetTransparency = (AniTime - m.emergence) / AniTime;
            m.c.current_x -= 50F * Settings.scale * (AniTime - m.emergence) / AniTime;
            m.c.drawScale *= (AniTime - m.emergence) / AniTime * 0.4F + 0.6F;
        }
        if (m.status == Mapcard.MapCardStatus.SUSPENDED) {
            m.c.transparency = m.c.targetTransparency = m.disappearance / AniTime;
            m.c.current_x += 50F * Settings.scale * (AniTime - m.disappearance) / AniTime;
            m.c.drawScale *= m.disappearance / AniTime * 0.4F + 0.6F;
        }

        if (isOutOfMon(m.c)) m.c.transparency = m.c.targetTransparency = 0.3F;

        ReflectionHacks.privateMethod(AbstractCard.class, "updateTransparency")
                .invoke(m.c);
        m.c.isGlowing = false;


        m.c.render(sb);
        if (m == hoveredMapcard)
            renderMapcardTip(m, sb);

        if (m != hoveredMapcard)
            sb.draw(pin,
                    m.c.current_x - (float) pin.packedWidth / 2.0F,
                    drawStartY + IMG_HEIGHT / 2.5F * m.drawScale - (float) pin.packedHeight / 2.0F,
                    (float) pin.packedWidth / 2.0F, (float) pin.packedHeight / 2.0F,
                    (float) pin.packedWidth, (float) pin.packedHeight,
                    Settings.scale * m.drawScale * 0.4F, Settings.scale * m.drawScale * 0.4F, 0.0F);

        m.hb.render(sb);
        if (cardInUseForTunerForm == m) {
            targetUIx = m.c.current_x;
            targetUIy = m.c.current_y;
        }

//            m.c.angle += 1;
        m.c.current_x = tempCurrentX;
        m.c.current_y = tempCurrentY;
        m.c.transparency = tempTransparency;
        m.c.targetTransparency = tempTargetTransparency;
        m.c.drawScale = tempScale;
        m.c.isGlowing = tempIsGlowing;
        ReflectionHacks.privateMethod(AbstractCard.class, "updateTransparency")
                .invoke(m.c);
        setOrResetGlowList(tempGlowBorders, m.c, false);
    }

    public void render(SpriteBatch sb) {
        if (hide) return;

        this.dorlach.render(sb);
        sb.setColor(color);
        for (Mapcard m : oldList) {
            if (m != hoveredMapcard) {
                renderAMapCard(m, sb);
            }
        }
        if (hoveredMapcard != null)
            renderAMapCard(hoveredMapcard, sb);

        //画飞行图标以及ui
        sb.setColor(Color.WHITE.cpy());
        if (AbstractDungeon.player.hasPower(TuningFormPower3.POWER_ID) && AbstractDungeon.player.getPower(TuningFormPower3.POWER_ID).amount > 0) {
            if (MaxCount > 0) {
                for (int i = 0; i < MaxCount; i++)
                    sb.draw(flight, rtTargetX(i) - (float) flight.packedWidth / 2.0F, drawStartY + IMG_HEIGHT * 0.4F - (float) flight.packedHeight / 2.0F,
                            (float) flight.packedWidth / 2.0F, (float) flight.packedHeight / 2.0F,
                            (float) flight.packedWidth, (float) flight.packedHeight,
                            Settings.scale * 0.75F, Settings.scale * 0.75F, 0.0F);
            }
        }

        if (cardInUseForTunerForm != null) {
            renderHoverReticle(sb, cardInUseForTunerForm.c);
        }

        if (isCardWaitingForSecondClickForTunerForm)
            renderSimpleTargetingUi(sb, targetUIx, targetUIy);
    }

    private Mapcard contain(AbstractCard c, Set<Mapcard> arr) {
        for (Mapcard m : arr) {
            if (m.c.uuid == c.uuid) return m;
        }
        return null;
    }


    private static class ReflectionCache {
        private static final Method updateTransparencyMethod;
        private static final Method updateColorMethod;

        static {
            try {
                updateTransparencyMethod = AbstractCard.class.getDeclaredMethod("updateTransparency");
                updateTransparencyMethod.setAccessible(true);
                updateColorMethod = AbstractCard.class.getDeclaredMethod("updateColor");
                updateColorMethod.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize reflection cache", e);
            }
        }
    }

    private void selfUpdate(AbstractCard c) {
        if (c.angle != c.targetAngle) {
            c.angle = MathHelper.angleLerpSnap(c.angle, c.targetAngle);
        }

        try {
            ReflectionCache.updateTransparencyMethod.invoke(c);
            ReflectionCache.updateColorMethod.invoke(c);
        } catch (Exception e) {
            ModHelper.logger.warn("Failed to update card properties", e);
        }
    }

    private float rtTargetX(int i) {
        float offset = 0;
        if (hoveredMapcard != null) {
            if (hoveredMapcard.index > i) offset = -AbstractCard.IMG_WIDTH_S * 0.15F;
            if (hoveredMapcard.index < i) offset = AbstractCard.IMG_WIDTH_S * 0.15F;
        }
        return drawStartX + i * padX + offset;
    }

    private void tunerFormPlayACard(AbstractCard c, Object target) {
        if (target instanceof Mapcard) {
            CustomTargeting.setCardTarget(c, target);
            AbstractDungeon.actionManager.addToBottom(
                    new PlayACardAction(c, AbstractDungeon.player.drawPile, null,
                            c instanceof TargetedElimination
                            ));
        }

        if (target instanceof AbstractMonster) {
            AbstractDungeon.actionManager.addToBottom(
                    new PlayACardAction(c, AbstractDungeon.player.drawPile, (AbstractMonster) target,
                            c instanceof TargetedElimination
                    ));
        }

        if (target == null) {
            AbstractDungeon.actionManager.addToBottom(
                    new PlayACardAction(c, AbstractDungeon.player.drawPile, null,
                            c instanceof TargetedElimination
                    ));
        }

        AbstractDungeon.actionManager.addToBottom(
                new AbstractGameAction() {
                    @Override
                    public void update() {

                        if(!(c instanceof TargetedElimination)) {
                            AbstractPower p = AbstractDungeon.player.getPower(TuningFormPower3.POWER_ID);
                            p.amount--;
                            p.flash();
                            p.updateDescription();
                        }

                        AT.this.cardInUseForTunerForm = null;
                        AT.this.isCardWaitingForSecondClickForTunerForm = false;
                        AT.this.isCardInPlayForTunerForm = false;
                        this.isDone = true;
                    }
                });
    }

    private void renderSimpleTargetingUi(SpriteBatch sb, float sx, float sy) {
        // 更新箭头位置
        Vector2 start = new Vector2(sx, sy);  // 起始点坐标
        Vector2 end = new Vector2(InputHelper.mX, InputHelper.mY);  // 终点坐标（鼠标位置）

        // 计算控制点（简化版，根据起点和终点自动计算）
        Vector2 control = new Vector2(
                start.x - (end.x - start.x) / 4.0F,
                end.y + (end.y - start.y) / 2.0F
        );

        // 决定箭头颜色和比例
        boolean hasTarget = hoveredMonster != null || hoveredMapcard != null;

        if (hasTarget) {
            sb.setColor(PURPLE);  // 如果有目标，使用金色
        } else {
            sb.setColor(Color.WHITE.cpy());  // 否则使用白色
        }

        // 绘制贝塞尔曲线
        drawSimpleCurvedLine(sb, start, end, control);

        // 计算箭头方向
        Vector2 direction = new Vector2(control.x - end.x, control.y - end.y).nor();

        // 在终点绘制箭头
        float arrowScale = hasTarget ? Settings.scale * 1.2F : Settings.scale;
        sb.draw(
                ImageMaster.TARGET_UI_ARROW,
                end.x - 128.0F, end.y - 128.0F,
                128.0F, 128.0F,
                256.0F, 256.0F,
                arrowScale, arrowScale,
                direction.angle() + 90.0F,
                0, 0, 256, 256,
                false, false
        );
    }

    private static void drawSimpleCurvedLine(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control) {
        // 曲线的点数（可以调整这个值来改变平滑度）
        final int POINTS = 20;
        Vector2[] points = new Vector2[POINTS];

        // 初始化点数组
        for (int i = 0; i < POINTS; i++) {
            points[i] = new Vector2();
        }

        // 计算每个点在贝塞尔曲线上的位置
        for (int i = 0; i < POINTS; i++) {
            float t = (float) i / (POINTS - 1);  // 参数范围 0 到 1
            points[i] = quadraticBezier(start, control, end, t);
        }

        // 沿曲线绘制圆点
        Vector2 direction = new Vector2();
        float baseRadius = 7.0F * Settings.scale;

        for (int i = 0; i < POINTS; i++) {
            // 计算每个点的半径（从小到大）
            float radius = baseRadius + i * 0.4F * Settings.scale;

            // 计算方向（用于旋转圆点）
            if (i > 0) {
                direction.x = points[i - 1].x - points[i].x;
                direction.y = points[i - 1].y - points[i].y;
            } else {
                direction.x = control.x - points[i].x;
                direction.y = control.y - points[i].y;
            }

            // 绘制圆点
            direction.nor();
            sb.draw(
                    ImageMaster.TARGET_UI_CIRCLE,
                    points[i].x - 64.0F, points[i].y - 64.0F,
                    64.0F, 64.0F,
                    128.0F, 128.0F,
                    radius / 18.0F, radius / 18.0F,
                    (i > 0) ? direction.angle() + 90.0F : direction.angle() + 270.0F,
                    0, 0, 128, 128,
                    false, false
            );
        }
    }

    private static Vector2 quadraticBezier(Vector2 start, Vector2 control, Vector2 end, float t) {
        Vector2 result = new Vector2();

        // 二次贝塞尔曲线公式: (1-t)²·P₀ + 2(1-t)t·P₁ + t²·P₂
        float mt = 1 - t;
        float mt2 = mt * mt;
        float t2 = t * t;

        result.x = mt2 * start.x + 2 * mt * t * control.x + t2 * end.x;
        result.y = mt2 * start.y + 2 * mt * t * control.y + t2 * end.y;

        return result;
    }

    private void renderHoverReticle(SpriteBatch sb, AbstractCard card) {
        switch (card.target) {
            case ENEMY:
                if (hoveredMonster != null) {
                    hoveredMonster.renderReticle(sb);
                }
                break;
            case ALL_ENEMY:
                AbstractDungeon.getCurrRoom().monsters.renderReticle(sb);
                break;
            case SELF:
                AbstractDungeon.player.renderReticle(sb);
                break;
            case SELF_AND_ENEMY:
                AbstractDungeon.player.renderReticle(sb);
                if (hoveredMonster != null) {
                    hoveredMonster.renderReticle(sb);
                }
                break;
            case ALL:
                AbstractDungeon.player.renderReticle(sb);
                AbstractDungeon.getCurrRoom().monsters.renderReticle(sb);
            case NONE:
        }

    }
}
