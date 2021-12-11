package main.me.volt.dvz.items.cosmetic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HatItems {
    public static ItemStack wolfHunterHat, santaHat, pharaohHat, goggles, warriorCap, dragonsBreathHat, jimmyCap, dwarvenBeard;

    public static void init() {
        setWolfHunterHat();
        setSantaHat();
        setPharaohHat();
        setGoggles();
        setWarriorCap();
        setDragonsBreathHat();
        setJimmyCap();
        setDwarvenBeard();
    }

    private static void setWolfHunterHat() {
        ItemStack item = new ItemStack(Material.PURPLE_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Wolf Hunter Hat");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        wolfHunterHat = item;
    }

    private static void setSantaHat() {
        ItemStack item = new ItemStack(Material.WHITE_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Santa " + ChatColor.GREEN + "Hat");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        santaHat = item;
    }

    private static void setPharaohHat() {
        ItemStack item = new ItemStack(Material.BROWN_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Pharaoh Hat");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        pharaohHat = item;
    }

    private static void setGoggles() {
        ItemStack item = new ItemStack(Material.GRAY_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Goggles");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        goggles = item;
    }

    private static void setWarriorCap() {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Warriors Hat");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        warriorCap = item;
    }

    private static void setDragonsBreathHat() {
        ItemStack item = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Dragons Breath Hat");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dragonsBreathHat = item;
    }

    private static void setJimmyCap() {
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Jimmy Cap");

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        jimmyCap = item;
    }

    private static void setDwarvenBeard() {
        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Dwarven Beard");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dwarvenBeard = item;
    }
}
