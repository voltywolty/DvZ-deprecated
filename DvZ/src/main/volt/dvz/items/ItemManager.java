package main.volt.dvz.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	public static ItemStack classSelector;
	
	public static ItemStack dwarfBuilderClass;
	public static ItemStack dwarfBuilderBook;
	
	public static ItemStack dwarfBlacksmithClass;
	public static ItemStack dwarfBlacksmithBook;
	
	public static ItemStack dwarfBakerClass;
	public static ItemStack dwarfBakerBook;
	
	public static ItemStack dwarfAlchemistClass;
	public static ItemStack dwarfAlchemistBook;
	
	public static ItemStack dwarfTailorClass;
	public static ItemStack dwarfTailorBook;
	
	public static void init() {
		createClassSelector();
		
		createDwarfBuilderClass();
		createDwarfBuilderBook();
		
		createDwarfBlacksmithClass();
		createDwarfBlacksmithBook();
		
		createDwarfBakerClass();
		createDwarfBakerBook();
		
		createDwarfAlchemistClass();
		createDwarfAlchemistBook();
		
		createDwarfTailorClass();
		createDwarfTailorBook();
	}
	
	private static void createClassSelector() {
		ItemStack item = new ItemStack(Material.MAGMA_CREAM, 1);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add("§7This magma cream gives you one or more random dwarf classes.");
		meta.setLore(lore);
		item.setItemMeta(meta);

		classSelector = item;
	}
	
	// BUILDER
	private static void createDwarfBuilderClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_13, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bBuilder Class");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Becoming a builder will allow you to build a keep for your fellow dwarfs.");
		lore.add("§7Make sure it fits everyone and protects the core!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfBuilderClass = item;
	}
	
	private static void createDwarfBuilderBook() {
		ItemStack item = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bBuilders Book");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Left click this book to receive your blocks and glowstone.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfBuilderBook = item;
	}
	
	// BLACKSMITH
	private static void createDwarfBlacksmithClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_BLOCKS, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bBlacksmith Class");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Becoming a blacksmith will allow you to craft weapons for other dwarfs.");
		lore.add("§7Be sure to set up near a tailor! They will supply you with gold ore.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfBlacksmithClass = item;
	}
	
	private static void createDwarfBlacksmithBook() {
		ItemStack item = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bBlacksmiths Book");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Left click this book to receive some random tools and ores.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfBlacksmithBook = item;
	}
	
	// BAKER
	private static void createDwarfBakerClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_CAT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bBaker Class");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Becoming a baker will allow you to make food for dwarfs!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfBakerClass = item;
	}
	
	private static void createDwarfBakerBook() {
		ItemStack item = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bBakers Book");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Left click this book to receive some food, coal, and clay.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfBakerBook = item;
	}
	
	// ALCHEMIST
	private static void createDwarfAlchemistClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_WAIT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bAlchemist Class");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Becoming an alchemist will allow you to craft potions for dwarfs.");
		lore.add("§7Set up shop near blacksmiths as they will supply you with redstone!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfAlchemistClass = item;
	}
	
	private static void createDwarfAlchemistBook() {
		ItemStack item = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bAlchemists Book");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Left click this book to receive some random potions, bones, and sand.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfAlchemistBook = item;
	}
	
	// TAILOR
	private static void createDwarfTailorClass() {
		ItemStack item = new ItemStack(Material.MUSIC_DISC_FAR, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bTailor Class");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Becoming a tailor allows you to craft armor for the other dwarves. You are very vital to every dwarf.");
		lore.add("§7It's a good idea to settle near an alchemist so they can supply you with bonemeal.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfTailorClass = item;
	}
	
	private static void createDwarfTailorBook() {
		ItemStack item = new ItemStack(Material.BOOK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§bTailors Book");
		
		List<String> lore = new ArrayList<>();
		lore.add("§7Left click this book to receive some gold and a random piece of armor.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		dwarfTailorBook = item;
	}
}
