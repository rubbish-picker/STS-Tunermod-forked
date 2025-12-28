//package tuner.powers;
//
//import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
//import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
//import com.megacrit.cardcrawl.core.AbstractCreature;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.localization.PowerStrings;
//import com.megacrit.cardcrawl.powers.AbstractPower;
//import tuner.helpers.ModHelper;
//
//import static com.badlogic.gdx.math.MathUtils.random;
//
//public class GremlinAccompanyPower extends AbstractPower {
//    public static final String POWER_ID = ModHelper.makeID(GremlinAccompanyPower.class.getSimpleName());
//    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
//    private static final String NAME = powerStrings.NAME;
//    // 能力的描述
//    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
//    private static final String PATH128 = "ModExampleResources/img/powers/SevitorPower84.png";
//    private static final String PATH48 = "ModExampleResources/img/powers/SevitorPower32.png";
//
//    public int[] list = {0,0,0,0,0}; //fw, 法师， debuff， 狡猾， 盾
//
//    public GremlinAccompanyPower(AbstractCreature owner, int amount) {
//        this.name = NAME;
//        this.ID = POWER_ID;
//        this.owner = owner;
//        this.type = PowerType.DEBUFF;
//        this.amount = amount;
//        loadRegion("minion");
//        this.updateDescription();
//
//        list = new int[]{0, 0, 0, 0, 0};
//        for(int i=0; i< amount; i++){
//            int randomNumber = random.nextInt(6);
//            list[randomNumber] +=1;
//        }
//    }
//
//    public void updateDescription() {
//        if (this.amount == 1) {
//            this.description = DESCRIPTIONS[0];
//        } else {
//            this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[0];
//        }
//    }
//}