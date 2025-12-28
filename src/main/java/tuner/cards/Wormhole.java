package tuner.cards;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.helpers.ModHelper;
import tuner.powers.RushPower;

import java.util.ArrayList;
import java.util.HashSet;

public class Wormhole extends MouldCard {
    public Wormhole() {
        super(Wormhole.class.getSimpleName(), 3, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
        this.damage = this.baseDamage = 6;
        this.selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        //需要一个酷酷音效
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_HEAVY));
    }

    @Override
    public void onRetained() {

        ArrayList<AbstractCardModifier> mods = new ArrayList<>();
        HashSet<String> modNames = new HashSet<>();

        for (AbstractCardModifier mod : CardModifierManager.modifiers(this)) {
            String str = mod.identifier(this);
            if (str.contains("tuner:") && !modNames.contains(str)) {
                mods.add(mod);
                modNames.add(str);
            }
        }
        if (mods.size() < 99999) {
            for (AbstractCardModifier mod : mods) {
                CardModifierManager.addModifier(this, mod);
            }
        }
        ModHelper.CalculateEffect(this);
    }


    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(9);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Wormhole();
    }
}
