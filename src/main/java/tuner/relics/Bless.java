package tuner.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import tuner.cards.colorless.Rewriting;
import tuner.helpers.ModHelper;

public class Bless extends CustomRelic {
    public static final String ID = ModHelper.makeID(Bless.class.getSimpleName());
    private static final String IMG = "tunerResources/img/relics/Bless.png";
    private static final String IMG_OTL = "tunerResources/img/relics/outline/Bless.png";

    private int num=0;

    public Bless() {
        super(ID, ImageMaster.loadImage(IMG), ImageMaster.loadImage(IMG_OTL), RelicTier.SPECIAL, LandingSound.MAGICAL);
        //super(ID, ImageMaster.loadImage(IMG), ImageMaster.loadImage(IMG_OTL), RelicTier.RARE, AbstractRelic.LandingSound.CLINK);
        this.counter = 1;
    }

    @Override
    public void atBattleStartPreDraw() {
        num = 0;
    }

    @Override
    public void atTurnStartPostDraw() {
        num++;
        if(num == 2){
            addToBot(new MakeTempCardInHandAction(new Rewriting(), this.counter));
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Bless();
    }
}
