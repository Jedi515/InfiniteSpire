package infinitespire.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import infinitespire.InfiniteSpire;
import infinitespire.perks.AbstractPerk;

public class AbstractPlayerEverythingPatch {
	@SpirePatch(cls="com.megacrit.cardcrawl.characters.AbstractPlayer", method="damage")
	public static class DamagePatch {
		@SpirePatch(cls = "com.megacrit.cardcrawl.characters.AbstractPlayer", method = "applyPreCombatLogic")
	    public static class ApplyPreCombatLogic
	    {
	        public static void Prefix(AbstractPlayer player) {
	            for (AbstractPerk perk : InfiniteSpire.allPerks.values()) {
	                if (perk.state.equals(AbstractPerk.PerkState.ACTIVE)) {
	                    perk.onCombatStart();
	                }
	            }
	        }
	    }
	    
	    @SpirePatch(cls = "com.megacrit.cardcrawl.characters.AbstractPlayer", method = "onVictory")
	    public static class OnVictory
	    {
	        public static void Prefix(AbstractPlayer player) {
	            for (AbstractPerk perk : InfiniteSpire.allPerks.values()) {
	                if (perk.state.equals(AbstractPerk.PerkState.ACTIVE)) {
	                    perk.onCombatVictory();
	                }
	            }
	        }
	    }
	    
	    @SpirePatch(cls = "com.megacrit.cardcrawl.characters.AbstractPlayer", method = "applyStartOfTurnRelics")
	    public static class ApplyStartOfTurnRelics
	    {
	        public static void Prefix(AbstractPlayer player) {
	            for (AbstractPerk perk : InfiniteSpire.allPerks.values()) {
	                if (perk.state.equals(AbstractPerk.PerkState.ACTIVE)) {
	                    perk.onTurnStart();
	                }
	            }
	        }
	    }
	    
	    @SpirePatch(cls = "com.megacrit.cardcrawl.characters.AbstractPlayer", method = "damage")
	    public static class Damage
	    {
	        @SpireInsertPatch(rloc = 25, localvars = { "damageAmount" })
	        public static void Insert(AbstractPlayer player, DamageInfo info, @ByRef int[] damageAmount) {
	        	if( info.owner == null) return;
	            for (AbstractPerk perk : InfiniteSpire.allPerks.values()) {
	            	if(perk.state.equals(AbstractPerk.PerkState.ACTIVE)) {
		                perk.onDamageTaken(info, damageAmount);
	            	}
	            }
	        }
	    }
	    
	    @SpirePatch(cls = "com.megacrit.cardcrawl.monsters.AbstractMonster", method = "damage")
	    public static class DamageDeal {
	    	@SpireInsertPatch(rloc = 30, localvars = { "damageAmount" })
	    	public static void Insert(AbstractMonster mon, DamageInfo info, @ByRef int[] damageAmount) {
	    		for (AbstractPerk perk : InfiniteSpire.allPerks.values()) {
	            	if(perk.state.equals(AbstractPerk.PerkState.ACTIVE)) {
		                perk.onDamageDealt(info, damageAmount);
	            	}
	            }
	    	}
	    }
	}
}