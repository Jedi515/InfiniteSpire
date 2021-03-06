package infinitespire.cards.black;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import infinitespire.InfiniteSpire;
import infinitespire.abstracts.BlackCard;
import infinitespire.actions.DrawCardAndUpgradeAction;

public class Collect extends BlackCard {

	private static final String ID = InfiniteSpire.createID("Collect");

	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	private static final String NAME = cardStrings.NAME;
	private static final String TEXTURE = "img/infinitespire/cards/collect.png";
	private static final int COST = 0;
	private static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int MAGIC = 3;
	
	public Collect() {
		super(ID, NAME, TEXTURE, COST, DESCRIPTION, CardType.SKILL, CardTarget.SELF);
		this.baseMagicNumber = MAGIC;
		this.magicNumber = MAGIC;
	}
	
	
	@Override
	public AbstractCard makeCopy() {
		return new Collect();
	}

	@Override
	public void upgrade() {
		if(!upgraded) {
			this.upgradeName();
			this.upgradeMagicNumber(1);
		}
	}

	@Override
	public void useWithEffect(AbstractPlayer p, AbstractMonster m) {
		AbstractDungeon.actionManager.addToBottom(new DrawCardAndUpgradeAction(p, this.magicNumber));
	}
}
