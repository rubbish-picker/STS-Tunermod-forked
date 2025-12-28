package tuner.misc;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import tuner.helpers.ConfigHelper;
import tuner.helpers.ModHelper;
import tuner.helpers.RandomImgCard;
import tuner.modCore.RewardTypeEnum;

public class ImaginaryReward extends CustomReward {
    private static final Texture ICON = new Texture("tunerResources/img/UI/imaginaryReward.png");
    private static final Texture ICON1 = new Texture("tunerResources/img/UI/Reward1.png");
    private static final Texture ICON2 = new Texture("tunerResources/img/UI/Reward2.png");
    private static final Texture ICON3 = new Texture("tunerResources/img/UI/Reward3.png");
    private static final String[] TEXT = (CardCrawlGame.languagePack.getUIString("tuner:ImaginaryReward")).TEXT;

    public int amount;
    private AbstractCard card;

    public ImaginaryReward(int amount) {
        super(ICON, TEXT[0], RewardTypeEnum.Tuner_HPREWARD);
        this.amount = amount;
        this.card = RandomImgCard.getImgCard(amount);
        if (this.card != null) {
            if (this.card.rarity == AbstractCard.CardRarity.COMMON)
                this.icon = ICON1;
            if (this.card.rarity == AbstractCard.CardRarity.UNCOMMON)
                this.icon = ICON2;
            if (this.card.rarity == AbstractCard.CardRarity.RARE)
                this.icon = ICON3;
        }
    }

    @Override
    public boolean claimReward() {
        this.cards.clear();
        AbstractCard c = RandomImgCard.getImgCard(amount);
        if (c != null) {
            this.cards.add(c);

            if (ConfigHelper.imgSelectSfx)
                ModHelper.playSfx("Hina_" + c.getClass().getSimpleName(), false, 0.3F);

            //            AbstractDungeon.cardRewardScreen.open(this.cards, this, TEXT[0]);
            AbstractDungeon.cardRewardScreen.customCombatOpen(this.cards, TEXT[0], true);
            AbstractDungeon.cardRewardScreen.rItem = this;
            AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
        }
        return false;
    }
}
