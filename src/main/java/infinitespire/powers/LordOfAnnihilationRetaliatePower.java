package infinitespire.powers;

import com.megacrit.cardcrawl.powers.AbstractPower;
import infinitespire.InfiniteSpire;
import infinitespire.monsters.LordOfAnnihilation;

public class LordOfAnnihilationRetaliatePower extends AbstractPower {

    LordOfAnnihilation gOwner;
    private boolean intentChanged = false;

    public LordOfAnnihilationRetaliatePower(LordOfAnnihilation owner){
        this.owner = owner;
        this.gOwner = owner;
        this.amount = -1;
        this.name = "Divine Shield";
        this.ID = "is_DivineShield";
        this.img = InfiniteSpire.getTexture("img/infinitespire/powers/retaliate.png");
        this.type = PowerType.BUFF;
        this.isTurnBased = true;
        this.updateDescription();
    }

    @Override
    public void atEndOfRound() {
        intentChanged = false;
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        for(AbstractPower p : owner.powers){
            if(!intentChanged && p.type == PowerType.DEBUFF) {
                gOwner.changeIntentToNuke();
                intentChanged = true;
                return;
            }
        }
    }

    @Override
    public void updateDescription(){
        this.description = "When ??? is inflicted with a debuff, he will retaliate with Ultimate Memoricia.";
    }
}
