package tuner.monsters;

import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.MinionPower;
import tuner.helpers.ModHelper;

public class MouldMonster extends CustomMonster {
    public static final String SIMPLE_NAME = MouldMonster.class.getSimpleName();
    public static final String ID = ModHelper.makeID(SIMPLE_NAME);
    public static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    //意图名称表，写了好看一点
    private static final byte TAP = 1;
    private static final byte TRIPLE_TAP = 2;
    private static final byte SHUFFLE = 3;

    public MouldMonster(int x, int y) {
        super(
                monsterStrings.NAME,
                ID,
                36,
                0.0F,
                0.0F,
                100.0F,
                100.0F,
                "GregoriusMod/img/monster/Choir.png",
                x,
                y
        );

        this.type = EnemyType.NORMAL;

        int hp = AbstractDungeon.ascensionLevel >= 8 ? 48 : 36;
        this.setHp(hp);

        //伤害队列，伤害在这里更新
        this.damage.add(new DamageInfo(this, AbstractDungeon.ascensionLevel >= 3 ? 9 : 6));
        this.damage.add(new DamageInfo(this, 3));
    }


    @Override
    public void usePreBattleAction() {
        this.addToBot(new ApplyPowerAction(this, this, new MinionPower(this)));
    }


    //回合行动，根据next move执行行动，
    // 伤害通过this.damage调用，this.damage的数值是会实时更新的
    // 执行完后手动调用getMove更新意图，或用roll move action更新意图
    @Override
    public void takeTurn() {
        if (this.nextMove == (byte) 0)
            this.addToBot(new TextAboveCreatureAction(this, monsterStrings.DIALOG[0]));
        else {
            if (this.nextMove == SHUFFLE) {
            }
        }

        this.addToBot(new RollMoveAction(this));
    }


    //更新意图，roll move action会调用这个并且传入一个0~99的随机数
    @Override
    protected void getMove(int num) {
        this.setMove(TRIPLE_TAP, Intent.ATTACK, this.damage.get(1).base, 3, true);
    }
}
