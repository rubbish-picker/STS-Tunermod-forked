package tuner.patches.tuner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.PowerStrings;
import tuner.relics.ATRelic;

import java.util.ArrayList;

public class Dorlach {
    private final Color colorDark = new Color(1.0F, 1.0F, 1.0F, 0.66F);
    private final Color colorLight = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private PowerStrings powerStrings;
    private Texture pic;
    public CardGroup dorlachGroup;

    public Hitbox hb;
    private boolean renderTip = false;
    private float x;
    private float y;
    private float flashTimer = 1F;
    private ArrayList<PowerTip> tips = new ArrayList();

    public Dorlach(float xx, float yy) {
        this.x = xx;
        this.y = yy;
        this.hb = new Hitbox(this.x, this.y,
                180F * Settings.scale, 180F * Settings.scale);

        powerStrings = CardCrawlGame.languagePack
                .getPowerStrings("Dorlach");

        if(AbstractDungeon.player.hasRelic(ATRelic.ID)) {

            int n = AbstractDungeon.player.getRelic(ATRelic.ID).counter;

            this.tips.add(new PowerTip(powerStrings.NAME,
                    powerStrings.DESCRIPTIONS[0]));
        }

        pic = new Texture("tunerResources/img/UI/MG.png");

        dorlachGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

//        for (AbstractCard c : CardLibrary.getAllCards()) {
//            if (c.color == ImaginaryColor) {
//                AbstractCard cc = c.makeCopy();
//                cc.upgrade();cc.upgrade();
//                dorlachGroup.addToBottom(cc);
//            }
//        }
    }

    public void setXY(float xxx, float yyy) {
        this.x = xxx;
        this.y = yyy;
        this.hb.move(xxx, yyy);
    }

    public void update() {
        this.hb.update();

        if (this.hb.hovered) {
            this.renderTip = true;
            if (InputHelper.justClickedLeft) this.hb.clickStarted = true;
        } else {
            this.renderTip = false;
            this.hb.clickStarted = false;
        }

        if (this.hb.clicked) {
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractDungeon.gridSelectScreen.open(dorlachGroup, 0, true, powerStrings.DESCRIPTIONS[1]);

                    dorlachGroup.glowCheck();
                    dorlachGroup.applyPowers();

                    this.isDone = true;
                }
            });
            this.hb.clicked = false;
        }

        if (this.flashTimer > 0.0F) {
            this.flashTimer -= Gdx.graphics.getDeltaTime();
        } else this.flashTimer = 1F;

    }

    public void render(SpriteBatch sb) {
        float angle = 50F;
        if (this.renderTip) angle = 52F;
        sb.setColor(Color.WHITE);
        sb.draw(pic, x, y - 240 * Settings.scale,
                0, 0, 800F, 800F,
                Settings.scale * 0.4F, Settings.scale * 0.4F,
                angle, 0, 0, 800, 800, false, false);


        // 渲染文本
        FontHelper.renderWrappedText(sb, FontHelper.energyNumFontBlue,
                Integer.toString(dorlachGroup.size()), x - 20 * Settings.scale, y - 50 * Settings.scale,
                1.0F, Color.WHITE, 0.8F);

        this.hb.render(sb);
        if (this.renderTip && !AbstractDungeon.isScreenUp) {
            TipHelper.queuePowerTips(this.hb.cX + 20.0F * Settings.scale, this.hb.cY + TipHelper.calculateAdditionalOffset(this.tips, this.hb.cY), this.tips);
        }

    }
}
