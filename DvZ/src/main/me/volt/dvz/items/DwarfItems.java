package main.me.volt.dvz.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DwarfItems {
    public static ItemStack dwarvenCompass;
    public static ItemStack sharedResourceChest;
    public static ItemStack lobbyMenuBook;

    public static void init() {
        createDwarvenCompass();
        createSharedResourceChest();
        createLobbyMenuBook();
    }

    private static void createDwarvenCompass() {
        ItemStack item = new ItemStack(Material.COMPASS, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Compass");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oRight click to find different locations.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dwarvenCompass = item;
    }

    private static void createSharedResourceChest() {
        ItemStack item = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bShared Resource Chest");

        List<String> lore = new ArrayList<>();
        lore.add("§6Left click to get access to the shared resources.");
        lore.add("§5§oYou can take and put items in this chest to share");
        lore.add("§5§owith other dwarves!");
        meta.setLore(lore);

        item.setItemMeta(meta);
        sharedResourceChest = item;
    }

    private static void createLobbyMenuBook() {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bMenu");

        List<String> lore = new ArrayList<>();
        lore.add("§5§oConfigure some settings and more.");
        meta.setLore(lore);

        item.setItemMeta(meta);
        lobbyMenuBook = item;
    }
}
