package infinitespire.perks;

import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.InputHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;

import infinitespire.InfiniteSpire;

public abstract class AbstractPerk {
	
	public static final Logger logger = LogManager.getLogger(InfiniteSpire.class.getName());
	
	public String name;
    public String id;
    public int tier;
    public PerkTreeColor tree;
    public PerkState state;
    public String description;
    private ArrayList<AbstractPerk> children = new ArrayList<AbstractPerk>();
    private ArrayList<AbstractPerk> parents = new ArrayList<AbstractPerk>();
    private ArrayList<PowerTip> tips;
    public final String[] parentIDs;
    private int cost = 500;
    
    private static final float textureSize = 175f, scrollWidth = 1520f, buttonSize = 120f;
    private static final float TEXT_OFFSET_X = textureSize / 2 * Settings.scale;
    private static final float TEXT_OFFSET_Y = 20f * Settings.scale;
    
    private float xPos, yPos, size, origSize, hitboxSize, width, hitboxOffset;
    protected float xOffset;
    
   	private Hitbox hitbox;
   	private Hitbox iconHitbox;
    
    public enum PerkTreeColor
    {
        RED, 
        GREEN, 
        BLUE; 
    }
    
    public enum PerkState {
    	LOCKED,
    	UNLOCKED,
    	ACTIVE
    }
    
    public AbstractPerk(String name, String id, String description, int tier, PerkTreeColor tree, String parent) {
    	this(name, id, description, tier, tree, new String[] {parent});
    }
    
    public AbstractPerk(String name, String id, String description, int tier, PerkTreeColor tree) {
    	this(name, id, description, tier, tree, new String[] {null});
    }
    
    public AbstractPerk(String name, String id, String description, int tier, PerkTreeColor tree, String[] parentIDs) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.tier = tier;
        this.tree = tree;
        this.parentIDs = parentIDs;
        this.tips = new ArrayList<PowerTip>();
        this.cost += (100 * tier);
        this.state = PerkState.LOCKED;
        this.tips.add(new PowerTip(name, description));
	    this.initializeTips();
        
        for(String parentID : parentIDs) {
	        if(parentID != null) {
	        	this.parents.add(InfiniteSpire.allPerks.get(parentID));
	        	for(AbstractPerk parent : parents) {
	        		parent.addToChildren(this);
	        	}
	    	}
        }
        
        size = textureSize * Settings.scale;
        origSize = size;
        width = scrollWidth * Settings.scale;
        xOffset = (Settings.WIDTH - width) / 2f;
        hitboxOffset = (size - hitboxSize) / 2f;
        hitboxSize = buttonSize * Settings.scale; 
	    yPos = (75f + ((buttonSize + 30) * tier)) * (Settings.scale);
	    xPos = 75f * Settings.scale;
	    
	    hitbox = new Hitbox(xPos + hitboxOffset, yPos + hitboxOffset, hitboxSize, hitboxSize);
	    iconHitbox = new Hitbox(32, 32);
	    
	    this.cost = 0;
    }
    
    //public abstract void use();
    
    public void onCombatStart() {
    }
    
    public void onCombatVictory() {
    }
    
    public void onCombatLoss() {
    }
    
    public void onTurnStart() {
    }
    
    public void onDamageDealt(final DamageInfo info, int[] damageAmount) {
    	
    }
    
    public void onDamageTaken(final DamageInfo info, int[] damageAmount) {
    
    }
    
    public void onAddPower(AbstractPower p) {
    	
    }
    
    public void onLoseBlock(int amount, boolean noAnimation) {
		
	}
    
    public void update() {
    	hitbox.update();
    	iconHitbox.update();

    	if(hitbox.justHovered) {
    		CardCrawlGame.sound.play("UI_HOVER");
    		this.size = origSize + 15;
    	}
    	
    	if(!hitbox.hovered) {
    		if(size > origSize) {
    			this.size -= 60 * Gdx.graphics.getDeltaTime();
    		}else if(size < origSize){
    			size = origSize;
    		}
    	}
    	
    	if(hitbox.hovered == true && InputHelper.justClickedLeft && this.state == PerkState.UNLOCKED && this.cost <= InfiniteSpire.points) {
    		CardCrawlGame.sound.play("UI_CLICK_1");
    		CardCrawlGame.sound.play("UNLOCK_PING");
        	this.state = PerkState.ACTIVE;
        	InfiniteSpire.points -= cost;
        	for(AbstractPerk child : this.children) {
        		child.state = PerkState.UNLOCKED;
        	}
        	for(AbstractPerk parent : parents) {
	        	if(parent != null) {
	        		for(AbstractPerk child : parent.children) {
	        			if(child.state == PerkState.UNLOCKED) {
	        				child.state = PerkState.LOCKED;
	        			}
	        		}
	        	}
        	}
    	}
    	
    	switch(tree) {
 		case BLUE:
 			xPos = ((width / 4f) * 3) - (size / 2f) + xOffset;
 			break;
 		case GREEN:
 			xPos = ((width) / 4f) - (size / 2f) + xOffset;
 			break;
 		case RED:
 			xPos = ((width) / 2f) - (size / 2f) + xOffset;
 			break;
 		default:
 			xPos = 100f;
 			break;
 	    	
 	    }
    	
    	hitboxSize = 120f * Settings.scale;
    	hitboxOffset = (size - hitboxSize) / 2f;
    	
    	hitbox.update(xPos + hitboxOffset, yPos + hitboxOffset);
    }

	public void render(SpriteBatch sb) {
		sb.setColor(Color.WHITE);
		
		Texture perkTexture = null;
		
    	perkTexture = getTextureByState();
    	
    	
    	
    	if(perkTexture != null)
    		sb.draw(perkTexture, xPos, yPos, size, size);
    	
    	renderCost(sb);
    	
    	if(hitbox.hovered) {
    		renderTip(sb);
    	}
    	
    	hitbox.render(sb);
	}
	
	protected void renderTip(SpriteBatch sb) {
		TipHelper.queuePowerTips(xPos + ((120f + (55f / 2f) + 5) * Settings.scale), yPos + ((120f + (55f / 2f) + 5) * Settings.scale), this.tips);
	}
	
	protected void renderTip(SpriteBatch sb, float x, float y) {
		TipHelper.queuePowerTips(x, y, this.tips);
	}
	
	public void renderInGame(SpriteBatch sb, float x, float y) {
		
		
		Texture icon = InfiniteSpire.getTexture("img/perks/" + this.tree.toString().toLowerCase() + "/" + this.id.toLowerCase() + ".png");
		sb.setColor(Color.WHITE);
		
		float yOffset = 12f;
		float xOffset = 12f;
		
		yOffset += (32f * tier);
		
		switch(tree) {
		case BLUE:
			xOffset = 64f;
			break;
		case RED:
			xOffset = 32f;
			break;
		case GREEN:
		default:
			xOffset = 0;
			break;
		
		}
		
		iconHitbox.update(x + xOffset, y - yOffset); 
		
		sb.draw(icon, x + xOffset, y - yOffset, 32.0f, 32.0f);
		
		if(iconHitbox.hovered) {
    		renderTip(sb, x + xOffset + 32f * Settings.scale, y - yOffset);
    	}
		
		iconHitbox.render(sb);
	}
	
	private void renderCost(SpriteBatch sb) {
		if(this.state != PerkState.UNLOCKED)
			return;
		
		sb.setColor(Color.WHITE);
		//sb.draw(ImageMaster.UI_GOLD, xPos + ICON_OFFSET_X, yPos + ICON_OFFSET_Y, ImageMaster.UI_GOLD.getWidth() * Settings.scale, ImageMaster.UI_GOLD.getWidth() * Settings.scale);
		
		Color color = Color.WHITE;
		
		
		if(this.cost > InfiniteSpire.points) color = Color.SALMON;
			
		FontHelper.renderFontCenteredTopAligned(sb, FontHelper.tipHeaderFont, cost + "", xPos + TEXT_OFFSET_X, yPos + TEXT_OFFSET_Y, color);
	}
	
	private Texture getTextureByState() {
		switch(state) {
		
		case LOCKED:
				return InfiniteSpire.getTexture("img/perks/" + this.tree.toString().toLowerCase() + "/locked.png");
			
		case UNLOCKED:
				return InfiniteSpire.getTexture("img/perks/" + this.tree.toString().toLowerCase()+ "/"+ this.id.toLowerCase() + ".png");
				
		case ACTIVE:
				return InfiniteSpire.getTexture("img/perks/" + this.tree.toString().toLowerCase() + "/" + this.id.toLowerCase() + "-active.png");
			
		default:
				return null;
		
		}
	}
	
	public void addToChildren(AbstractPerk perk) {
		children.add(perk);
	}
	
	protected void initializeTips() {
        final Scanner desc = new Scanner(this.description);
        while (desc.hasNext()) {
            String s = desc.next();
            if (s.charAt(0) == '#') {
                s = s.substring(2);
            }
            s = s.replace(',', ' ');
            s = s.replace('.', ' ');
            s = s.trim();
            s = s.toLowerCase();
            boolean alreadyExists = false;
            if (GameDictionary.keywords.containsKey(s)) {
                s = GameDictionary.parentWord.get(s);
                for (final PowerTip t : this.tips) {
                    if (t.header.toLowerCase().equals(s)) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (alreadyExists) {
                    continue;
                }
                this.tips.add(new PowerTip(TipHelper.capitalize(s), GameDictionary.keywords.get(s)));
            }
        }
        desc.close();
    }
	
	public int getCost() {
		return Integer.valueOf(cost + "");
	}

	
}