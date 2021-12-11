package main.me.volt.dvz.listeners;

import main.me.volt.dvz.DvZ;

import main.me.volt.dvz.gui.KitGUI;
import main.me.volt.dvz.items.DwarfItems;
import main.me.volt.dvz.items.DwarfLoadoutItems;
import main.me.volt.dvz.items.cosmetic.HatItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

// NOTE - this class is ONLY used for GUI related
// events - anything different needs to go into
// GameListener
public class GUIListener implements Listener {
    DvZ plugin;

    public GUIListener(DvZ plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMonsterMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("monster_menu") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            ItemStack clicked = event.getCurrentItem();
            Material clickedType = clicked.getType();

            if (clickedType == Material.WOODEN_SWORD) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.BOW) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.ARROW) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.CYAN_DYE) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.BONE) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.SLIME_BALL) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.PRISMARINE_CRYSTALS) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.GOLDEN_PICKAXE) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.IRON_HOE) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
        }
    }

    @EventHandler
    public void onDwarfMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        if (!event.getView().getTitle().equals("Select a Kit - BETA")) {
            if (this.plugin.dwarves.contains(player)) {
                if (clicked.getType() != null && clicked.getType() == Material.ARROW) {
                    event.setCancelled(true);
                }
                else if (clicked.getType() != null && clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                    event.setCancelled(true);
                }
                else {
                    event.setCancelled(false);
                }
            }
            else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onHatMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        HatItems hats = new HatItems();

        if (event.getView().getTitle().equals("Select a Hat") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            if (clicked == null || !clicked.hasItemMeta())
                return;

            if (clicked.getType() == Material.PURPLE_WOOL) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.wolfHunterHat);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.wolfHunterHat);
                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.GRAY_WOOL) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.goggles);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.goggles);
                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.LIGHT_GRAY_WOOL) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.warriorCap);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.warriorCap);
                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.RED_WOOL) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.dragonsBreathHat);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.dragonsBreathHat);
                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.BROWN_WOOL) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.pharaohHat);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.pharaohHat);
                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.DIAMOND_HELMET) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.jimmyCap);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Jimmy Cap")){
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.jimmyCap);
                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.YELLOW_STAINED_GLASS) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.dwarvenBeard);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.dwarvenBeard);
                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.WHITE_WOOL) {
                if (clicked.getItemMeta().getLore().contains(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)")) {
                    player.getInventory().setHelmet(null);
                    event.setCurrentItem(hats.santaHat);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    hatEquipped(item);

                    player.getInventory().setHelmet(hats.santaHat);
                    event.setCurrentItem(item);
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDwarfLoadoutClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (event.getView().getTitle().equals("Select a Kit") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            if (clicked == null || !clicked.hasItemMeta())
                return;

            if (clicked.getType() == Material.BROWN_DYE) {
                if (clicked.getItemMeta().getLore().contains("§d(Click to Un-equip Class)") || this.plugin.rangerKit.contains(player) || this.plugin.paladinKit.contains(player)) {
                    GUIListener.this.plugin.warriorKit.remove(player);

                    event.setCurrentItem(DwarfItems.warriorKit);
                    event.getInventory().setItem(45, DwarfLoadoutItems.pointsRemaining);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    GUIListener.this.plugin.warriorKit.add(player);
                    GUIListener.this.plugin.rangerKit.remove(player);
                    GUIListener.this.plugin.paladinKit.remove(player);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    warriorUnequipped(item);

                    ItemStack points = event.getInventory().getItem(45);
                    ItemMeta pointsMeta = points.getItemMeta();
                    points.setAmount(1);
                    points.setItemMeta(pointsMeta);

                    event.getInventory().setItem(45, points);

                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.STONE_SWORD) {
                if (clicked.getItemMeta().getLore().contains("§d(Click to Un-equip Class)") || this.plugin.rangerKit.contains(player) || this.plugin.warriorKit.contains(player)) {
                    GUIListener.this.plugin.paladinKit.remove(player);

                    event.setCurrentItem(DwarfItems.paladinKit);
                    event.getInventory().setItem(45, DwarfLoadoutItems.pointsRemaining);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    GUIListener.this.plugin.warriorKit.remove(player);
                    GUIListener.this.plugin.rangerKit.remove(player);
                    GUIListener.this.plugin.paladinKit.add(player);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    paladinUnequipped(item);

                    ItemStack points = event.getInventory().getItem(45);
                    ItemMeta pointsMeta = points.getItemMeta();
                    points.setAmount(1);
                    points.setItemMeta(pointsMeta);

                    event.getInventory().setItem(45, points);

                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.BOW) {
                if (clicked.getItemMeta().getLore().contains("§d(Click to Un-equip Class)") || this.plugin.paladinKit.contains(player) || this.plugin.warriorKit.contains(player)) {
                    GUIListener.this.plugin.rangerKit.remove(player);

                    event.setCurrentItem(DwarfItems.rangerKit);
                    event.getInventory().setItem(45, DwarfLoadoutItems.pointsRemaining);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    GUIListener.this.plugin.rangerKit.add(player);
                    GUIListener.this.plugin.warriorKit.remove(player);
                    GUIListener.this.plugin.paladinKit.remove(player);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    rangerUnequipped(item);

                    ItemStack points = event.getInventory().getItem(45);
                    ItemMeta pointsMeta = points.getItemMeta();
                    points.setAmount(1);
                    points.setItemMeta(pointsMeta);

                    event.getInventory().setItem(45, points);

                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.MELON_SEEDS) {
                player.openInventory(KitGUI.kitGUI);
            }
            event.setCancelled(true);
        }

        if (event.getView().getTitle().equals("Dwarf Loadout") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            if (clicked.getType() == Material.PUMPKIN_SEEDS) {
                player.openInventory(KitGUI.kitSelectorGUI);
            }
            else if (clicked.getType() == Material.MELON_SEEDS) {
                player.openInventory(KitGUI.kitMeleeGUI);
            }
            event.setCancelled(true);
        }
    }

    public static void hatEquipped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.LUCK, 1);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "(Click to Un-equip Hat)");
        meta.setLore(lore);

        item.setItemMeta(meta);
    }

    public static void warriorUnequipped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Un-equip Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oWarriors are the fighters of the front line.");
        lore.add("§5§oThey wield a powerful sword, this sword gives PROC for 3 seconds.");
        meta.setLore(lore);

        item.setAmount(1);
        item.setItemMeta(meta);
    }

    public static void paladinUnequipped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Un-equip Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oPaladins are the protecting the light.");
        lore.add("§5§oThey wield a powerful mace to hold the line and tools");
        lore.add("§5§oto call forth the light to battle.");
        meta.setLore(lore);

        item.setAmount(1);
        item.setItemMeta(meta);
    }

    public static void rangerUnequipped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Un-equip Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oRangers are the defenders of the keep.");
        lore.add("§5§oThey wield a powerful bow, heal allies, and repair");
        lore.add("§5§othe walls in desperate situations against the demons.");
        meta.setLore(lore);

        item.setAmount(1);
        item.setItemMeta(meta);
    }
}
