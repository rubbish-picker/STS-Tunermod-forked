package tuner.cardDep;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import tuner.cards.MouldCard;
import tuner.helpers.ModHelper;

public class TheSandOfTheUniverse extends MouldCard {

    private boolean onMap = false;

    public TheSandOfTheUniverse() {
        super(TheSandOfTheUniverse.class.getSimpleName(), 4, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 30;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT));
    }

    @Override
    public void update() {
        super.update();

        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && ModHelper.rtATgroup().contains(this)) {
            onMaping();
        } else {
            this.onMap = false;
        }
    }

    public void onMaping() {
        if (!this.onMap) {
            onMap = true;
            this.modifyCostForCombat(-1);
            this.flash();
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(8);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new TheSandOfTheUniverse();
    }
}
