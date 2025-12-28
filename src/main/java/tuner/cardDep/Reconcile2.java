package tuner.cardDep;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.action.LoadAHandAction;
import tuner.action.RewriteAction;
import tuner.cards.MouldCard;
import tuner.helpers.ModHelper;

public class Reconcile2 extends MouldCard {

    private static final Texture changedImg = new Texture("tunerResources/img/cards/Reconcile3.png");
    private static final Texture changingImg = new Texture("tunerResources/img/cards/Reconcile2.png");

    private boolean isChanged;
    private boolean display;

    public Reconcile2(boolean display, boolean isChanged) {
        super(Reconcile2.class.getSimpleName(), 1, CardType.ATTACK, CardRarity.BASIC, CardTarget.ENEMY);
        this.damage = this.baseDamage = 9;

        this.display = display;
        change(isChanged);
    }

    public Reconcile2() {
        this(true, false);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn),
                AbstractGameAction.AttackEffect.BLUNT_LIGHT));

        if (isChanged)
            addToBot(new LoadAHandAction(1));
        else if (ModHelper.canRewrote()) {
            addToBot(new RewriteAction(p.drawPile.getTopCard()));
        }

        change(!isChanged);
    }

    public void change(boolean isChanged) {
        if (isChanged) {
            if (upgraded) {
                this.rawDescription = EXTENDED_DESCRIPTION[2];
            } else this.rawDescription = EXTENDED_DESCRIPTION[1];
            this.name = EXTENDED_DESCRIPTION[0];
            this.portrait = new TextureAtlas.AtlasRegion(changedImg, 0, 0, 250, 190);
        } else {
            if (upgraded) {
                this.rawDescription = DESCRIPTION_UPG;
            } else this.rawDescription = DESCRIPTION;
            this.name = cardStrings.NAME;
            this.portrait = new TextureAtlas.AtlasRegion(changingImg, 0, 0, 250, 190);
        }

        if (this.display) {
            AbstractCard c = new Reconcile2(false, !isChanged);
            if (this.upgraded) c.upgrade();
            this.cardsToPreview = c;
        } else this.cardsToPreview = null;

        this.isChanged = isChanged;

        initializeDescription();
        if (upgraded) {
            this.name = this.name + "+";
            this.initializeTitle();
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
            change(isChanged);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Reconcile2();
    }
}
