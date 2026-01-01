package tuner.misc.option;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import static com.megacrit.cardcrawl.helpers.ImageMaster.loadImage;

public class OptionGainMaxHp extends AbstractCampfireOption {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    /** Max HP gained when using this campfire option. */
    public static final int MAX_HP_GAIN = 10;

    public OptionGainMaxHp(boolean available) {
        this.label = TEXT[0];
        this.description = available ? String.format(TEXT[1], MAX_HP_GAIN) : TEXT[2];
        this.usable = available;
        this.img = loadImage("tunerResources/img/UI/maxHP.png");
    }

    @Override
    public void useOption() {
        // Keep behavior consistent with other campfire options in this mod (see OptionA).
        com.megacrit.cardcrawl.dungeons.AbstractDungeon.effectList.add(new CampfireGainMaxHpEffect(MAX_HP_GAIN));
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("tuner:OptionGainMaxHp");
        TEXT = uiStrings.TEXT;
    }
}
