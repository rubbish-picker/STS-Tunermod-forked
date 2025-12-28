package tuner.powers;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import tuner.cards.BreakingThroughTheArmy;
import tuner.cards.Tracer;
import tuner.cards.Twinkle;
import tuner.helpers.ModHelper;

import java.util.HashSet;

public class FusionPower extends AbstractPower {
    public static final String POWER_ID = ModHelper.makeID(FusionPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    private static final String NAME = powerStrings.NAME;
    // 能力的描述
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";

    public static int IDIndex;

    private AbstractCard a;
    private CardGroup.CardGroupType ag;
    private boolean atom;

    public FusionPower(AbstractCard a) {
        this.name = NAME;
        this.ID = POWER_ID + IDIndex;
        IDIndex++;
        this.owner = AbstractDungeon.player;
        this.type = PowerType.BUFF;

        this.a = a;

        atom = false;

        loadRegion("attackBurn");
        this.updateDescription();
    }

    @Override
    public void update(int slot) {
        super.update(slot);

        this.ag = CardGroup.CardGroupType.UNSPECIFIED;

        if (!atom) {
            for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
//                if (c == a && !(c instanceof Tracer || c instanceof BreakingThroughTheArmy || c instanceof Twinkle)) {
                if (c == a) {
                    ag = CardGroup.CardGroupType.DISCARD_PILE;
                    break;
                }
            }
            if (ag == CardGroup.CardGroupType.DISCARD_PILE) {
                atom = true;
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        FusionPower.this.flash();
                        atom = false;
                        ModHelper.loadACard(a, AbstractDungeon.player.discardPile);
                        this.isDone = true;
                    }
                });
            }
        }

    }

    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.a.name + DESCRIPTIONS[1];
    }
}