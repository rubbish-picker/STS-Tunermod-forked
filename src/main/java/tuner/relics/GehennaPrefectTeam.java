package tuner.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import tuner.helpers.ModHelper;

public class GehennaPrefectTeam extends CustomRelic {
    public static final String ID = ModHelper.makeID(GehennaPrefectTeam.class.getSimpleName());
    private static final String IMG = "tunerResources/img/relics/GehennaPrefectTeam.png";
    private static final String IMG_OTL = "tunerResources/img/relics/outline/GehennaPrefectTeam.png";

    public GehennaPrefectTeam() {
        super(ID, ImageMaster.loadImage(IMG), ImageMaster.loadImage(IMG_OTL), RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public void obtain() {
        AbstractPlayer player = AbstractDungeon.player;
        player.relics.stream()
                .filter(r -> r instanceof MemoryFragments)
                .findFirst()
                .map(r -> player.relics.indexOf(r))
                .ifPresent(index
                        -> instantObtain(player, index, true));

        this.flash();
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player.hasRelic(MemoryFragments.ID);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.energy.energyMaster++;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.energy.energyMaster--;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GehennaPrefectTeam();
    }
}
