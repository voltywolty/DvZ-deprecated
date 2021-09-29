package main.volt.dvz.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MonsterItemManager {
	public static ItemStack monsterClassSelector;
	public static ItemStack suicidePill;
	
	public static ItemStack zombieClass;
	
	public static ItemStack spiderClass;
	
	public static ItemStack creeperClass;
	public static ItemStack gunpowderItem;
	
	public static ItemStack skeletonClass;
	
	public static ItemStack wolfClass;
	
	public static ItemStack broodmotherClass;
	
	public static ItemStack chickenNuggetClass;
	
	public static ItemStack hungryPigClass;
	
	public static ItemStack cougarClass;
	
	// DRAGON / ADMIN ONLY
	public static ItemStack dragonFireball;
	public static ItemStack lightningStick;
	
	public static void init() {
		createMonsterClassSelector();
		createSuicidePill();
		
		createZombieClass();
		
		createSpiderClass();
		
		createCreeperClass();
		createGunpowder();
		
		createSkeletonClass();
		
		createWolfClass();
		
		createBroodmotherClass();
		
		createChickenNuggetClass();
		
		createHungryPigClass();
		
		createCougarClass();
		
		createDragonFireball();
		createLightningStick();
	}
	
	private static void createMonsterClassSelector() {
		ItemStack item = new ItemStack(Material.GOLD_NUGGET, 1);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This nugget grants you a random monster class.");
		meta.setLore(lore);
		item.setItemMeta(meta);

		monsterClassSelector = item;
	}
	
	private static void createSuicidePill() {
		ItemStack item = new ItemStack(Material.GHAST_TEAR, 1);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Don't like your class? Left click to return to spawn.");
		meta.setLore(lore);
		item.setItemMeta(meta);

		suicidePill = item;
	}
	
	private static void createZombieClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_11);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bZombie Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the zombie class. This is the most common class.");
		lore.add("§7Attack the dwarves and show no mercy.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		zombieClass = item;
	}
	
	
	private static void createSpiderClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_CHIRP);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bSpider Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the spider class.");
		lore.add("§7You get a spider eye that can make the Dwarf's go blind.");
		lore.add("§7The spider eye can also make them dizzy and hurts the Dwarf for 5 seconds.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		spiderClass = item;
	}
	
	// CREEPER CLASS
	private static void createCreeperClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_BLOCKS, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bCreeper Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the creeper class.");
		lore.add("§7You get gunpowder that when you left click, it takes two seconds to blow up.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		creeperClass = item;
	}
	
	private static void createGunpowder() {
		ItemStack item = new ItemStack(Material.GUNPOWDER, 1);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This gunpowder explodes the area around you.");
		lore.add("§7It takes two seconds to blow up.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		gunpowderItem = item;
	}
	
	// SKELETON CLASS
	private static void createSkeletonClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_CAT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bSkeleton Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the skeleton class.");
		lore.add("§7You get a bow, some vines, and a stack of arrows.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		skeletonClass = item;
	}
	
	// WOLF CLASS
	private static void createWolfClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_FAR, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bWolf Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the wolf class.");
		lore.add("§7You get two swords, wolf eggs, and 64 bones.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		wolfClass = item;
	}
	
	// BROODMOTHER CLASS
	private static void createBroodmotherClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_MALL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bBroodmother Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the broodmother class.");
		lore.add("§7Lay your eggs and cause the dwarf wall to fall!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		broodmotherClass = item;
	}
	
	// CHICKEN NUGGET CLASS
	private static void createChickenNuggetClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_MELLOHI, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bChicken Nugget Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the chicken nugget class.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		chickenNuggetClass = item;
	}
	
	// HUNGRY PIG CLASS
	private static void createHungryPigClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_WARD, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bHungry Pig Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the hungry pig class.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		hungryPigClass = item;
	}
	
	// COUGAR CLASS
	private static void createCougarClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_STAL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bCougar Class§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This disc gives you the cougar class.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		cougarClass = item;
	}
	
	private static void createDragonFireball() {
		ItemStack item = new ItemStack(Material.FIRE_CHARGE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§4Dragons Fireball§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§5Left clicking this will allow you to spit fireballs at dwarves.§");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dragonFireball = item;
	}
	
	private static void createLightningStick() {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§4Lightning Stick§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§5Left clicking this will summon lighting. You can scare the dwarves.§");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		lightningStick = item;
	}
}
