package tuner.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import tuner.characters.Tuner;
import tuner.effects.ShowATEffect;
import tuner.helpers.ConfigHelper;
import tuner.helpers.RandomImgCard;
import tuner.misc.ImaginaryReward;
import tuner.helpers.ModHelper;
import tuner.misc.option.OptionA;

import java.util.ArrayList;
import java.util.HashSet;

import static tuner.modCore.CardTypeEnum.Imaginary;

public class ATRelic extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = ModHelper.makeID(ATRelic.class.getSimpleName());
    private static final String IMG = "tunerResources/img/relics/Hakkero.png";
    private static final String IMG_OTL = "tunerResources/img/relics/outline/Hakkero.png";

    public static tuner.patches.tuner.AT at;

    public Integer actNum = 0;

    public ATRelic() {
        super(ID, ImageMaster.loadImage(IMG), ImageMaster.loadImage(IMG_OTL), RelicTier.SPECIAL, AbstractRelic.LandingSound.CLINK);
        //super(ID, ImageMaster.loadImage(IMG), ImageMaster.loadImage(IMG_OTL), RelicTier.RARE, AbstractRelic.LandingSound.CLINK);
    }

    @Override
    public void atBattleStartPreDraw() {
        if (at == null)
            at = new tuner.patches.tuner.AT();

        AbstractDungeon.effectList.add(new ShowATEffect(false));

        ArrayList<AbstractCard> list = (ArrayList<AbstractCard>) AbstractDungeon.player.masterDeck.group.clone();
        at.dorlach.dorlachGroup.clear();
        for (AbstractCard c : list) {
            if (c.type == Imaginary) {
                at.dorlach.dorlachGroup.addToBottom(c.makeSameInstanceOf());
            }
        }

//        int num = -1;
//        for (AbstractCard c : at.dorlach.dorlachGroup.group) {
//            num++;
//            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(c.makeCopy(), rtXposition(num), rtYposition(num)));
//        }


        //生成奖励
        boolean hasBoss = false;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m.type == AbstractMonster.EnemyType.BOSS)
                hasBoss = true;
        }

        int cardInt;
        ArrayList<AbstractCard> tmpList;
        if (!ConfigHelper.difficultMod) {
            if (hasBoss) {

                int i = RandomImgCard.getRandomCard(0, 0, 1).index;
                AbstractDungeon.getCurrRoom().rewards.add(new ImaginaryReward(i));

            } else if (AbstractDungeon.getCurrRoom().eliteTrigger) {

                int i = RandomImgCard.getRandomCard(0, 8, 2).index;
                AbstractDungeon.getCurrRoom().rewards.add(new ImaginaryReward(i));

            } else {
                if (ModHelper.selfRamdom.random(0, 1) == 0) {

                    int i = RandomImgCard.getRandomCard(12, 7, 1).index;
                    AbstractDungeon.getCurrRoom().rewards.add(new ImaginaryReward(i));

                }
            }
        } else {
            if (hasBoss) {

                int i = RandomImgCard.getRandomCard(0, 0, 1).index;
                AbstractDungeon.getCurrRoom().rewards.add(new ImaginaryReward(i));

            } else if (AbstractDungeon.getCurrRoom().eliteTrigger) {

                int i = RandomImgCard.getRandomCard(4, 4, 2).index;
                AbstractDungeon.getCurrRoom().rewards.add(new ImaginaryReward(i));

            }
        }


        //给其他玩家惩罚
        if (!(AbstractDungeon.player instanceof Tuner))
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                        if (!m.isDeadOrEscaped()) {
                            addToTop(new ApplyPowerAction(m, m, new StrengthPower(m, 1)));
                        }
                    }
                    this.isDone = true;
                }
            });
    }


    @Override
    public void renderCounter(SpriteBatch sb, boolean inTopPanel) {
        int n = 0;
        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck != null)
            for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
                if (c.type == Imaginary) n++;
        this.counter = n;
        super.renderCounter(sb, inTopPanel);
    }

    private float rtXposition(int index) {
        index = index / 2;
        if (index % 2 == 1) {
            return Settings.WIDTH * 0.5F + (float) ((index + 1) / 2) * 260.0F * Settings.scale;
        } else {
            return Settings.WIDTH * 0.5F - (float) ((index + 1) / 2) * 260.0F * Settings.scale;
        }
    }

    private float rtYposition(int index) {
        if (index % 2 == 1) return Settings.HEIGHT * 0.33F;
        else return Settings.HEIGHT * 0.67F;
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        boolean temp = false;
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.type == Imaginary) {
                temp = true;
            }
        }
//        if (this.actNum < AbstractDungeon.actNum)
        options.add(new OptionA(temp));
    }

    @Override
    public Integer onSave() {
        return this.actNum;
    }

    @Override
    public void onLoad(Integer b) {
        this.actNum = b;
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new ATRelic();
    }
}
