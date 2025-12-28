//package tuner.monsters;
//
//import basemod.abstracts.CustomMonster;
//import com.badlogic.gdx.math.MathUtils;
//import com.megacrit.cardcrawl.actions.AbstractGameAction;
//import com.megacrit.cardcrawl.actions.common.*;
//        import com.megacrit.cardcrawl.actions.utility.SFXAction;
//import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.cards.DamageInfo;
//import com.megacrit.cardcrawl.cards.status.Burn;
//import com.megacrit.cardcrawl.cards.status.Dazed;
//import com.megacrit.cardcrawl.cards.status.VoidCard;
//import com.megacrit.cardcrawl.cards.status.Wound;
//import com.megacrit.cardcrawl.core.AbstractCreature;
//import com.megacrit.cardcrawl.core.CardCrawlGame;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
//import com.megacrit.cardcrawl.localization.MonsterStrings;
//import com.megacrit.cardcrawl.monsters.AbstractMonster;
//import com.megacrit.cardcrawl.powers.*;
//        import com.megacrit.cardcrawl.relics.AbstractRelic;
//import com.megacrit.cardcrawl.vfx.TextAboveCreatureEffect;
//import tuner.helpers.ModHelper;
//
//public class Organ extends CustomMonster {
//    public static final String SIMPLE_NAME = Organ.class.getSimpleName();
//    public static final String ID = ModHelper.makeID(SIMPLE_NAME);
//    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
//
//    public Organ(int x, int y) {
//        super(
//                monsterStrings.NAME,
//                ID,
//                3,
//                0.0F,
//                0.0F,
//                100.0F,
//                100.0F,
//                "tunerResources/img/monster/Organ.png",
//                x,
//                y
//        );
//
//        this.type = EnemyType.NORMAL;
//
//        int hp = AbstractDungeon.ascensionLevel >= 8 ? 3 : 2;
//        this.setHp(hp);
//    }
//
//    @Override
//    public void usePreBattleAction() {
//        this.addToBot(new ApplyPowerAction(this, this, new MinionPower(this)));
//    }
//
//    @Override
//    public void takeTurn() {
//        if (this.nextMove == (byte) 0)
//            this.addToBot(new TextAboveCreatureAction(this, monsterStrings.DIALOG[0]));
//        else {
//            if (this.nextMove == SHUFFLE) {
//                AbstractCard card;
//
//                int index = AbstractDungeon.getMonsters().monsters.indexOf(this);
//                if (index == 0)
//                    card = new Burn();
//                else if (index == 1)
//                    card = new VoidCard();
//                else if (index == 3)
//                    card = new Dazed();
//                else
//                    card = new Wound();
//
//                if (AbstractDungeon.ascensionLevel >= 17)
//                    this.addToBot(new MakeTempCardInDrawPileAction(card, 1, true, false,
//                            false, Settings.WIDTH * (index + 1) * 0.2F, Settings.HEIGHT * 0.5F));
//                else
//                    this.addToBot(new MakeTempCardInDiscardAction(card, 1));
//            }
//            else if (this.nextMove == TAP)
//                this.addToBot(new DamageAction(AbstractDungeon.player, this.damage.get(0),
//                        AbstractGameAction.AttackEffect.FIRE));
//            else if (this.nextMove == TRIPLE_TAP) {
//                for (int i = 0; i < 3; i++)
//                    this.addToBot(new DamageAction(AbstractDungeon.player, this.damage.get(1),
//                            AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
//            }
//        }
//
//        this.addToBot(new RollMoveAction(this));
//    }
//
//    @Override
//    protected void getMove(int num) {
//        if (this.currentHealth == 1)
//            this.setMove((byte) 0, Intent.NONE);
//        else {
//            if (this.startOfBattle) {
//                this.setMove(SHUFFLE, Intent.DEBUFF);
//                this.startOfBattle = false;
//            }
//            else if (num >= 50 && !this.lastTwoMoves(TAP))
//                this.setMove(TAP, Intent.ATTACK, this.damage.get(0).base);
//            else
//                this.setMove(TRIPLE_TAP, Intent.ATTACK, this.damage.get(1).base, 3, true);
//        }
//    }
//
//    @Override
//    public void heal(int healAmount) {
//        int cur = this.currentHealth;
//
//        super.heal(healAmount);
//
//        if (cur == 1 && this.currentHealth > 1) {
//            this.rollMove();
//            this.createIntent();
//            this.addToBot(new SetMoveAction(this, this.nextMove, this.intent));
//        }
//    }
//
////	@Override
////	public void damage(DamageInfo info) {
////		super.damage(info);
////	}
//
//    @Override
//    public void die() {
//        if (!AbstractDungeon.getCurrRoom().cannotLose) {
//            AbstractMonster g = AbstractDungeon.getMonsters().monsters.stream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (!this.forceToExit && this.hasPower(InvulnerabilityPower.POWER_ID) &&
//                    g != null && !g.isDeadOrEscaped()) {
//                this.currentHealth = 1;
//                if (this.maxHealth < 1)
//                    this.maxHealth = 1;
//                this.healthBarUpdatedEvent();
//
//                if (this.nextMove != (byte) 0) {
//                    this.setMove((byte) 0, Intent.NONE);
//                    this.createIntent();
//                    this.addToBot(new SetMoveAction(this, (byte) 0, Intent.NONE));
//                }
//            }
//            else
//                super.die();
//        }
//    }
//}
