package tuner.powers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.helpers.ModHelper;

public class RushPower extends AbstractPower {
    private static final int RATE = 3;

    public static final String POWER_ID = ModHelper.makeID(RushPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = ModHelper.makeRlcAd(RushPower.class.getSimpleName(), true);
    private static final String PATH48 = ModHelper.makeRlcAd(RushPower.class.getSimpleName(), false);

    private static final Texture UI = new Texture("tunerResources/img/UI/Rush.png");

    private int formalDamage;

    public RushPower(AbstractCreature owner, int amt) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.type = PowerType.BUFF;

        this.amount = amt;
        this.formalDamage = this.amount / RATE;

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(PATH128), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(PATH48), 0, 0, 32, 32);

        this.updateDescription();
    }


    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        super.renderAmount(sb, x, y, c);

        if (this.amount > 0) {
            x = this.owner.drawX - 170 * Settings.scale;
            y = this.owner.drawY + 30 * Settings.scale;
            sb.setColor(Color.WHITE);
            sb.draw(UI, x, y, (float) UI.getWidth(), (float) UI.getHeight());

            FontHelper.renderWrappedText(sb, FontHelper.energyNumFontBlue,
                    Integer.toString(this.amount), x, y + 3 * Settings.scale, this.fontScale, c, 0.8F);
            if (!this.owner.hasPower(JiabanPower.POWER_ID))
                FontHelper.renderWrappedText(sb, FontHelper.energyNumFontBlue,
                        Integer.toString(additionalDamage()), x, y + 38 * Settings.scale, this.fontScale, c, 0.8F);

        }
    }

    @Override
    public int onLoseHp(int damageAmount) {
        if (amount >= damageAmount) {
            this.addToTop(new ReducePowerAction(owner, owner, POWER_ID, damageAmount));
            return 0;
        } else {
            this.addToTop(new RemoveSpecificPowerAction(owner, owner, POWER_ID));
            return damageAmount - amount;
        }
    }

    public void reducePower(int reduceAmount) {
        this.fontScale = 8.0F;
        this.amount -= reduceAmount;
        if (this.amount == 0) {
            this.addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, NAME));
        }

        if (this.amount >= 999) {
            this.amount = 999;
        }

        if (this.amount <= -999) {
            this.amount = -999;
        }
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL &&
                !this.owner.hasPower(JiabanPower.POWER_ID)) {
            return damage + additionalDamage();
        }
        return damage;
    }

    public int additionalDamage() {
        int temp;

        if (this.owner.hasPower(HighDimensionalExistencePower.POWER_ID)) {
            temp = this.amount * (1 + this.owner.getPower(HighDimensionalExistencePower.POWER_ID).amount) / RATE;
        } else temp = this.amount / RATE;

        if (temp != formalDamage) {
            formalDamage = temp;
            updateDescription();
        }
        return temp;
    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1]
                + formalDamage + DESCRIPTIONS[2];
    }
}