package main.me.volt.dvz.gui;

import main.me.volt.dvz.items.DwarfLoadoutItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class KitGUI {
    public static Inventory kitGUI = Bukkit.createInventory(null, 54, "Dwarf Loadout");
    public static Inventory kitMeleeGUI = Bukkit.createInventory(null, 54, "Dwarf Loadout | Melee");

    public KitGUI() {

    }

    public static boolean openGUI(Player player) {
        // DEFAULT
        kitGUI.setItem(0, DwarfLoadoutItems.dwarvenRunebladeEquipped);
        kitGUI.setItem(1, DwarfLoadoutItems.dwarvenShortbowEquipped);
        kitGUI.setItem(2, DwarfLoadoutItems.jimmyJuiceEquipped);
        kitGUI.setItem(3, DwarfLoadoutItems.dwarvenArmorEquipped);
        kitGUI.setItem(4, DwarfLoadoutItems.magicStone1Equipped);
        kitGUI.setItem(5, DwarfLoadoutItems.enchantedLamps1Equipped);
        kitGUI.setItem(6, DwarfLoadoutItems.wigglyWrenches1Equipped);
        kitGUI.setItem(7, DwarfLoadoutItems.stoneMason1Equipped);
        kitGUI.setItem(8, DwarfLoadoutItems.torchbearer1Equipped);


        // DIVIDERS
        kitGUI.setItem(9, DwarfLoadoutItems.divider);
        kitGUI.setItem(10, DwarfLoadoutItems.divider);
        kitGUI.setItem(11, DwarfLoadoutItems.divider);
        kitGUI.setItem(12, DwarfLoadoutItems.divider);
        kitGUI.setItem(13, DwarfLoadoutItems.divider);
        kitGUI.setItem(14, DwarfLoadoutItems.divider);
        kitGUI.setItem(15, DwarfLoadoutItems.divider);
        kitGUI.setItem(16, DwarfLoadoutItems.divider);
        kitGUI.setItem(17, DwarfLoadoutItems.divider);

        kitGUI.setItem(48, DwarfLoadoutItems.leftArrow);
        kitGUI.setItem(50, DwarfLoadoutItems.rightArrow);

        player.openInventory(kitGUI);
        return true;
    }

    public static boolean openMeleeGUI(Player player) {
        kitMeleeGUI.setItem(0, DwarfLoadoutItems.dwarvenArmorEquipped);

        player.openInventory(kitMeleeGUI);
        return true;
    }
}
