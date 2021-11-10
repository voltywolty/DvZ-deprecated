package main.me.volt.dvz.screens;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LobbyScreen implements InventoryHolder {
    public Inventory menuInventory;
    public Inventory titleInventory;

    public LobbyScreen() {
        menuInventory = Bukkit.createInventory(this, 9, "Menu Screen");
        titleInventory = Bukkit.createInventory(this, 54, "Title Screen");

        init();
    }

    private void init() {
        ItemStack item;

        List<String> lore = new ArrayList<>();
        lore.add("§5§oSelect a custom title in game.");
        item = createItem("§bTitle Screen", Material.NAME_TAG, lore);
        menuInventory.setItem(4, item);

        item = createItem("§bPaladin", Material.DIAMOND_AXE, Collections.singletonList("§5§oPaladins are about protecting the light. They wield a powerful mace to hold the line and tools to call forth the light to battle.\""));
        titleInventory.setItem(22, item);
    }

    private ItemStack createItem(String name, Material mat, List<String> lore) {
        ItemStack item = new ItemStack(mat, 1);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public Inventory getInventory() {
        return menuInventory;
    }
}
