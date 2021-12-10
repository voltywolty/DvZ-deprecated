package main.me.volt.dvz.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DwarfItems {
    public static ItemStack dwarvenCompass;
    public static ItemStack sharedResourceChest;

    public static ItemStack classSelectorBook;
    public static ItemStack hatSelector;

    public static ItemStack wolfHunterHat;
    public static ItemStack santaHat; // USE ON CHRISTMAS ONLY!
    public static ItemStack pharaohHat;
    public static ItemStack goggles;
    public static ItemStack warriorCap;
    public static ItemStack dragonsBreathHat;
    public static ItemStack dwarvenCap;
    public static ItemStack dwarvenBeard;
    public static ItemStack clearHat;

    public static ItemStack warriorKit;
    public static ItemStack paladinKit;
    public static ItemStack rangerKit;
    public static ItemStack clearKit;

    public static ItemStack inventorySlotHolder;

    public static void init() {
        createDwarvenCompass();
        createSharedResourceChest();

        createClassSelectorBook();
        createHatSelector();

        createWolfHunterHat();
        createSantaHat();
        createPharaohHat();
        createGoggles();
        createWarriorCap();
        createDragonsBreath();
        createDwarvenCap();
        createDwarvenBeard();
        createClearHat();

        createWarriorClass();
        createPaladinClass();
        createRangerClass();
        createClearKit();

        createInventorySlotHolder();
    }

    private static void createDwarvenCompass() {
        ItemStack item = new ItemStack(Material.COMPASS, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Compass");

        List<String> lore = new ArrayList<>();
        lore.add("§eR-Click: Get directions to a given location.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dwarvenCompass = item;
    }

    private static void createSharedResourceChest() {
        ItemStack item = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bShared Resource Chest");

        List<String> lore = new ArrayList<>();
        lore.add("§eL-Click: Get access to the shared resources.");
        lore.add("§5§oYou can take and put items in this chest to share");
        lore.add("§5§owith other dwarves!");
        meta.setLore(lore);

        item.setItemMeta(meta);
        sharedResourceChest = item;
    }

    private static void createClassSelectorBook() {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bClass Selector");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oPick a class you wanna be before the game starts.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        classSelectorBook = item;
    }

    private static void createHatSelector() {
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bHat Selector");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oPick a hat you want to show off during them match.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        hatSelector = item;
    }

    private static void createWolfHunterHat() {
        ItemStack item = new ItemStack(Material.PURPLE_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bWolf Hunter");

        item.setItemMeta(meta);
        wolfHunterHat = item;
    }

    private static void createSantaHat() {
        ItemStack item = new ItemStack(Material.WHITE_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven §cSanta §aHat");

        item.setItemMeta(meta);
        santaHat = item;
    }

    private static void createPharaohHat() {
        ItemStack item = new ItemStack(Material.BROWN_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bPharaoh");

        item.setItemMeta(meta);
        pharaohHat = item;
    }

    private static void createGoggles() {
        ItemStack item = new ItemStack(Material.GRAY_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bGnomish Goggles");

        item.setItemMeta(meta);
        goggles = item;
    }

    private static void createWarriorCap() {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bWarrior");

        item.setItemMeta(meta);
        warriorCap = item;
    }

    private static void createDragonsBreath() {
        ItemStack item = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDragons Breath");

        item.setItemMeta(meta);
        dragonsBreathHat = item;
    }

    private static void createDwarvenCap() {
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Cap");
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        dwarvenCap = item;
    }

    private static void createDwarvenBeard() {
        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Beard");

        item.setItemMeta(meta);
        dwarvenBeard = item;
    }

    private static void createClearHat() {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cRemove Hat");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oClears any hat you have equipped.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        clearHat = item;
    }

    private static void createWarriorClass() {
        ItemStack item = new ItemStack(Material.BROWN_DYE, 64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bWarrior Kit");

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Select Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oWarriors are the fighters of the front line.");
        lore.add("§5§oThey wield a powerful sword, this sword gives PROC for 3 seconds.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        warriorKit = item;
    }

    private static void createPaladinClass() {
        ItemStack item = new ItemStack(Material.STONE_SWORD, 64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bPaladin Kit");

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Select Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oPaladins are the protecting the light.");
        lore.add("§5§oThey wield a powerful mace to hold the line and tools");
        lore.add("§5§oto call forth the light to battle.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        paladinKit = item;
    }

    private static void createRangerClass() {
        ItemStack item = new ItemStack(Material.BOW, 64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bRanger Kit");

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Select Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oRangers are the defenders of the keep.");
        lore.add("§5§oThey wield a powerful bow, heal allies, and repair");
        lore.add("§5§othe walls in desperate situations against the demons.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        rangerKit = item;
    }

    private static void createClearKit() {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cClear Kit");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oClears the kit you currently have selected.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        clearKit = item;
    }

    private static void createInventorySlotHolder() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");

        item.setItemMeta(meta);
        inventorySlotHolder = item;
    }
}
