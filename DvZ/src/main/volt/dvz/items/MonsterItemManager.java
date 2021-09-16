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
	
	// DRAGON / ADMIN ONLY
	public static ItemStack dragonFireball;
	public static ItemStack lightningStick;
	
	public static void init() {
		createMonsterClassSelector();
		createSuicidePill();
		
		createZombieClass();
		
		createSpiderClass();
		
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
		lore.add("§7Don't like your class? Right click to return to spawn.");
		meta.setLore(lore);
		item.setItemMeta(meta);

		suicidePill = item;
	}
	
	private static void createZombieClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_STAL);
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
		ItemStack item = new ItemStack(Material.MUSIC_DISC_STRAD);
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
	
	private static void createDragonFireball() {
		ItemStack item = new ItemStack(Material.FIRE_CHARGE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§4Dragons Fireball§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§5Right clicking this will allow you to spit fireballs at dwarves.§");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dragonFireball = item;
	}
	
	private static void createLightningStick() {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§4Lightning Stick§");
		
		List<String> lore = new ArrayList<>();
		lore.add("§5Right clicking this will summon lighting. You can scare the dwarves.§");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		lightningStick = item;
	}
}
