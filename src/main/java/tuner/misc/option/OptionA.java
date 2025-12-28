package tuner.misc.option;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import static com.megacrit.cardcrawl.helpers.ImageMaster.loadImage;

public class OptionA extends AbstractCampfireOption {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;

    public OptionA(boolean available) {
        this.label = TEXT[0];
        this.description = available ? TEXT[1] : TEXT[2];
        this.usable = available;
        this.img = loadImage("tunerResources/img/UI/train.png");
    }

    public void useOption() {
        AbstractDungeon.effectList.add(new CampfireGetEffect());
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("tuner:OptionA");
        TEXT = uiStrings.TEXT;
    }
}
