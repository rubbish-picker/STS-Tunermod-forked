package tuner.cards.imaginaryColor;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Predator extends MouldCard {
    public Predator() {
        super(Predator.class.getSimpleName(), 1, Imaginary, CardRarity.COMMON, CardTarget.NONE, ImaginaryColor);
        this.magicNumber = this.baseMagicNumber = 8;
        this.tags.add(CardTags.HEALING);
        ModHelper.initDes(this);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                ArrayList<AbstractMonster> list = new ArrayList<>();
                for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
                    if (!mo.isDeadOrEscaped()) list.add(mo);
                }

                if (!list.isEmpty()) {
                    list.sort(Comparator.comparingDouble(AbstractMonster -> AbstractMonster.drawX));

                    // 计算中间索引
                    int size = list.size();
                    int mid = size / 2;

                    // 获取中间的1到2个元素
                    List<AbstractMonster> midElements;
                    if (size % 2 == 0) {
                        midElements = list.subList(mid - 1, mid + 1); // 偶数个元素，取中间两个
                    } else {
                        midElements = list.subList(mid, mid + 1); // 奇数个元素，取中间一个
                    }

                    for(AbstractMonster mo : midElements){
                        addToBot(new DamageAction(mo, new DamageInfo(AbstractDungeon.player, this.owner.magicNumber, DamageInfo.DamageType.THORNS),
                                AbstractGameAction.AttackEffect.FIRE));
                    }
                }
            }
        };
    }

    @Override
    public AbstractCard makeCopy() {
        return new Predator();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        return false;
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
        if(ModHelper.imgUpgradeName(this)){
            upgradeMagicNumber(3);
            ModHelper.initDes(this);
        }
    }
}
