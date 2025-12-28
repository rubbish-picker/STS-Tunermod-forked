package tuner.cardDep;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import tuner.cards.MouldCard;
import tuner.cards.imaginaryColor.mod.AbstractMod;
import tuner.helpers.ModHelper;

import static tuner.modCore.CardColorEnum.ImaginaryColor;
import static tuner.modCore.CardTypeEnum.Imaginary;

public class Hologram extends MouldCard {
    public Hologram() {
        super(Hologram.class.getSimpleName(), 1, Imaginary, CardRarity.RARE, CardTarget.NONE, ImaginaryColor);
        this.tags.add(CardTags.HEALING);
        this.modifier = new AbstractMod(this) {
            @Override
            public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
                if (!card.exhaust) {
                    addToBot(new AbstractGameAction() {
                        @Override
                        public void update() {
                            addToBot(new AbstractGameAction() {
                                @Override
                                public void update() {
                                    CardGroup cg = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);

                                    for (AbstractCard c : AbstractDungeon.player.hand.group) {
                                        if (c == card) {
                                            cg = AbstractDungeon.player.hand;
                                            break;
                                        }
                                    }
                                    for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
                                        if (c == card) {
                                            cg = AbstractDungeon.player.hand;
                                            break;
                                        }
                                    }
                                    for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                                        if (c == card) {
                                            cg = AbstractDungeon.player.hand;
                                            break;
                                        }
                                    }
                                    for (AbstractCard c : AbstractDungeon.player.exhaustPile.group) {
                                        if (c == card) {
                                            cg = AbstractDungeon.player.hand;
                                            break;
                                        }
                                    }

                                    if (cg.type != CardGroup.CardGroupType.UNSPECIFIED)
                                        ModHelper.loadACard(card, cg);
                                    this.isDone = true;
                                }
                            });
                            this.isDone = true;
                        }
                    });
                }
            }
        };

    }

    @Override
    public AbstractCard makeCopy() {
        return new Hologram();
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
    }
}
