package tuner.monsters;


import basemod.ReflectionHacks;
import basemod.abstracts.CustomMonster;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.actions.tempHp.AddTemporaryHPAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.blue.MachineLearning;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Cultist;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;
import tuner.patches.monsters.FontHelperPatch;
import tuner.powers.ServantOfTheServantsOfTheSaintsPower;

public class Glgl extends CustomMonster {
    public static final String SIMPLE_NAME = Glgl.class.getSimpleName();
    public static final String ID = tuner.helpers.ModHelper.makeID(SIMPLE_NAME);
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final byte SUMMON_CHOIRS = 0; // 庄严之唱诗班
    private static final byte WHISPERS_OF_THE_SAINT = 1; // 圣者之紊语
    private static final byte SOLEMN_REPENTANCE = 2; // 肃穆之悔悟

    private int summonCount = 0;


    public Glgl() {
        super(
                monsterStrings.NAME,
                ID,
                5000,
                0.0F,
                0.0F,
                350.0F,
                320.0F,
                "tunerResource/img/monster/Gregorius.png",
                0.0F,
                200.0F
        );

        this.type = EnemyType.BOSS;

        this.setHp(AbstractDungeon.ascensionLevel >= 9 ? 5000 : 4000);

        this.damage.add(new DamageInfo(this, AbstractDungeon.ascensionLevel >= 4 ? 30 : 20)); // 普世之救赎
    }

    @Override
    public void usePreBattleAction() {
        CardCrawlGame.music.unsilenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        CardCrawlGame.music.silenceTempBgmInstantly();
        AbstractDungeon.getCurrRoom().playBgmInstantly("Gregorius_Symphony.mp3");

        this.addToBot(new ApplyPowerAction(this, this,
                new InvinciblePower(this, 600)));

        this.addToBot(new ApplyPowerAction(this, this,
                new ServantOfTheServantsOfTheSaintsPower(this)));
//        this.addToBot(new ApplyPowerAction(this, this, new SanctusPower(this)));
    }

//    private void summonChoir(int x, int y, boolean strengthen) {
//        Choir choir = new Choir(x, y, false, strengthen);
//
//        choir.init();
//        choir.applyPowers();
//
//        AbstractDungeon.getMonsters().addMonster(0, choir);
//
//        if (ModHelper.isModEnabled("Lethality")) {
//            this.addToBot(new ApplyPowerAction(choir, choir, new StrengthPower(choir, 3), 3));
//        }
//
//        if (ModHelper.isModEnabled("Time Dilation")) {
//            this.addToBot(new ApplyPowerAction(choir, choir, new SlowPower(choir, 0)));
//        }
//
//        choir.showHealthBar();
//        choir.usePreBattleAction();
//
//        if (this.hasPower(SanctusPower.POWER_ID)) {
//            for (AbstractPower power : this.powers)
//                if (power.type == AbstractPower.PowerType.DEBUFF) {
//                    AbstractPower copy = SanctusHelper.makeCopy(power, choir);
//                    if (copy != null && copy.owner == choir) {
//                        this.addToBot(new ApplyPowerAction(choir, this, copy));
//                    }
//                }
//        }
//    }

    @Override
    public void takeTurn() {
        if(this.nextMove == SUMMON_CHOIRS) {
            //balabalabala
            getMove(WHISPERS_OF_THE_SAINT);
        }else if (this.nextMove == WHISPERS_OF_THE_SAINT) {
            //balabalabala
            getMove(SOLEMN_REPENTANCE);
        }else if (this.nextMove == SOLEMN_REPENTANCE) {
            //balabalabala
            getMove(WHISPERS_OF_THE_SAINT);
        }
    }


    @Override
    protected void getMove(int num) {
        if (num == SUMMON_CHOIRS) {
            this.setMove(monsterStrings.MOVES[SUMMON_CHOIRS],
                    SUMMON_CHOIRS,
                    Intent.UNKNOWN);
        } else if (num == WHISPERS_OF_THE_SAINT) {
            this.setMove(monsterStrings.MOVES[WHISPERS_OF_THE_SAINT],
                    WHISPERS_OF_THE_SAINT,
                    Intent.ATTACK_BUFF, this.damage.get(0).base);
        } else if (num == SOLEMN_REPENTANCE) {
            this.setMove(monsterStrings.MOVES[SOLEMN_REPENTANCE],
                    SOLEMN_REPENTANCE,
                    Intent.DEBUFF);
        }
    }

    @Override
    public void die() {
        if (!AbstractDungeon.getCurrRoom().cannotLose) {
            for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) ;
//                if (m instanceof Choir) {
//                    ((Choir) m).forceToExit = true;
//                    this.addToBot(new InstantKillAction(m));
//                }

            super.die();

//            this.onBossVictoryLogic();
//            this.onFinalBossVictoryLogic();
//            CardCrawlGame.stopClock = true;
        }
    }

    @Override
    public void renderHealth(SpriteBatch sb) {
        super.renderHealth(sb);

        if (!Settings.hideCombatElements) {
            AbstractPower servantPower = this.getPower(ServantOfTheServantsOfTheSaintsPower.POWER_ID);
            if (servantPower != null) {
                Color backup = sb.getColor();

                float hbYOffset = ReflectionHacks.getPrivate(this, AbstractCreature.class, "hbYOffset");

                float x = this.hb.cX + this.hb.width / 2.0F;
                float y = this.hb.cY - this.hb.height / 2.0F + hbYOffset;

                sb.setColor(Color.WHITE);
                sb.draw(ImageMaster.BLOCK_ICON,
                        x - 54.0F, y - 54.0F,
                        54.0F, 54.0F,
                        108.0F, 108.0F,
                        Settings.scale, Settings.scale,
                        0.0F, 0, 0, 64, 64,
                        false, false);

                String str = ((ServantOfTheServantsOfTheSaintsPower) servantPower).getColor();

                if (str.equals("r"))
                    sb.setColor(Color.RED);
                else if (str.equals("y"))
                    sb.setColor(Color.YELLOW);
                else if (str.equals("g"))
                    sb.setColor(Color.GREEN);

                sb.draw(ImageMaster.BLOCK_ICON,
                        x - 44.0F, y - 44.0F,
                        44.0F, 44.0F,
                        88.0F, 88.0F,
                        Settings.scale, Settings.scale,
                        0.0F, 0, 0, 64, 64,
                        false, false);

                int count = (int) this.powers.stream()
                        .filter(p -> p.type == AbstractPower.PowerType.DEBUFF)
                        .count();

                sb.setColor(Color.WHITE);
                FontHelper.renderFontCentered(sb,
                        str.equals("r") ? FontHelper.energyNumFontRed :
                                str.equals("y") ? FontHelperPatch.energyNumFontYellow :
                                        FontHelper.energyNumFontGreen,
                        String.valueOf(count),
                        x, y,
                        Color.WHITE, 1.0F);

                sb.setColor(backup);
            }
        }
    }
}
