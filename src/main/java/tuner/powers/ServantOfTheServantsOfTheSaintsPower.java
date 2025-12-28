package tuner.powers;


import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.helpers.ModHelper;

import java.util.ArrayList;

public class ServantOfTheServantsOfTheSaintsPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(ServantOfTheServantsOfTheSaintsPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    private ArrayList<Integer> green = new ArrayList<>(), yellow = new ArrayList<>();
    private TextureAtlas.AtlasRegion imgR84, imgR32;
    private TextureAtlas.AtlasRegion imgY84, imgY32;
    private TextureAtlas.AtlasRegion imgG84, imgG32;

    private int damage;

    public ServantOfTheServantsOfTheSaintsPower(AbstractCreature owner) {
        this.ID = POWER_ID;
        this.name = powerStrings.NAME;
        this.owner = owner;
        this.type = NeutralPowertypePatch.NEUTRAL;

        this.imgR84 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants_R", true)), 0, 0, 84, 84);
        this.imgR32 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants_R", false)), 0, 0, 32, 32);

        this.imgY84 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants_Y", true)), 0, 0, 84, 84);
        this.imgY32 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants_Y", false)), 0, 0, 32, 32);

        this.imgG84 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants_G", true)), 0, 0, 84, 84);
        this.imgG32 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants_G", false)), 0, 0, 32, 32);

        this.region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants", true)), 0, 0, 84, 84);
        this.region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage(ModHelper.makeRlcAd("Servants", false)), 0, 0, 32, 32);

        if (AbstractDungeon.ascensionLevel < 19) {
            this.damage = 2;
        }else this.damage = 3;

        this.green.add(3);

        this.yellow.add(2);
        this.yellow.add(4);

        this.updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = String.format(powerStrings.DESCRIPTIONS[0],
                this.green.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse(""),
                this.yellow.stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse(""), this.damage, this.damage);

        String color = this.getColor();
        if (color.equals("r")) {
            this.region128 = this.imgR84;
            this.region48 = this.imgR32;
        } else if (color.equals("y")) {
            this.region128 = this.imgY84;
            this.region48 = this.imgY32;
        } else {
            this.region128 = this.imgG84;
            this.region48 = this.imgG32;
        }
    }

    @Override
    public void renderAmount(SpriteBatch sb, float x, float y, Color c) {
        this.amount = (int) this.owner.powers.stream()
                .filter(p -> p.type == PowerType.DEBUFF)
                .count();

        String color = this.getColor();

        ReflectionHacks.setPrivate(this, AbstractPower.class, "greenColor",
                color.equals("r") ? Color.RED.cpy() :
                        color.equals("y") ? Color.YELLOW.cpy() :
                                Color.GREEN.cpy());

        super.renderAmount(sb, x, y, c);

        this.amount = -1;
    }

    public String getColor() { // "r", "y", "g"
        int count = (int) AbstractDungeon.player.powers.stream()
                .filter(p -> p.type == PowerType.BUFF)
                .count();

        if (this.green.contains(count))
            return "g";
        else if (this.yellow.contains(count))
            return "y";
        else
            return "r";
    }

    @Override
    public void update(int slot) {
        this.updateDescription();
        super.update(slot);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        String color = this.getColor();

        if (color.equals("y")){
            this.flash();
            addToBot(new DamageAction(AbstractDungeon.player, new DamageInfo(this.owner, this.damage, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
        } else if (color.equals("r")) {
            this.flash();
            addToBot(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, this.damage));
        }

    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        String color = this.getColor();

        if (color.equals("y"))
            return damage * 2.0F;
        else if (color.equals("g"))
            return damage * 3.0F;
        else
            return damage;
    }
}
