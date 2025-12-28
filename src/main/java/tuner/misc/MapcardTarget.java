package tuner.misc;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.cards.targeting.TargetingHandler;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.megacrit.cardcrawl.cards.AbstractCard;
import tuner.helpers.ModHelper;
import tuner.patches.tuner.Mapcard;
import tuner.relics.ATRelic;

public class MapcardTarget extends TargetingHandler<Mapcard> {
    public static AbstractCard getTarget(AbstractCard card) {
        if(CustomTargeting.getCardTarget(card) != null)
            return ((Mapcard)CustomTargeting.getCardTarget(card)).c;
        else{
            if(ModHelper.canRewrote()){
                return ModHelper.getRamdomTarget();
            }
            return null;
        }
    }
    public Mapcard hovered = null;
    @Override
    public boolean hasTarget() {
        return hovered != null;
    }
    @Override
    public void updateHovered() {
        hovered = null;

        if (ModHelper.canRewrote() && ATRelic.at.hoveredMapcard != null){
            hovered = ATRelic.at.hoveredMapcard;
        }
    }
    @Override
    public Mapcard getHovered() {
        return hovered;
    }
    @Override
    public void clearHovered() {
        hovered = null;
    }
    @Override
    public void renderReticle(SpriteBatch sb) {
        if (hovered != null) {
            //显示准星
        }
    }
}