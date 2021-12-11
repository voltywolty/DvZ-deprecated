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
        meta.setDisplayName("§bOpen Kit Menu");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oClick to open the kit menu.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        classSelectorBook = item;
    }

    private static void createHatSelector() {
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bOpen Hat Menu");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oClick to open the hat menu.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);
        hatSelector = item;
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
