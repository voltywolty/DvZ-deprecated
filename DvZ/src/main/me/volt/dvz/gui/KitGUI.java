package main.me.volt.dvz.gui;

import main.me.volt.dvz.items.DwarfItems;
import main.me.volt.dvz.items.DwarfLoadoutItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class KitGUI {
    public static Inventory kitSelectorGUI = Bukkit.createInventory(null, 54, "Select a Kit");
    public static Inventory kitGUI = Bukkit.createInventory(null, 54, "Dwarf Loadout");
    public static Inventory kitMeleeGUI = Bukkit.createInventory(null, 54, "Dwarf Loadout | Melee");

    public KitGUI() {

    }

    public static void kitSelectorItems() {
        KitGUI.kitSelectorGUI.setItem(0, DwarfItems.warriorKit);
        KitGUI.kitSelectorGUI.setItem(1, DwarfItems.paladinKit);
        KitGUI.kitSelectorGUI.setItem(2, DwarfItems.rangerKit);

        KitGUI.kitSelectorGUI.setItem(45, DwarfLoadoutItems.pointsRemaining);
        KitGUI.kitSelectorGUI.setItem(49, DwarfLoadoutItems.rightArrow);
    }

    public static void dwarfLoadoutItems() {
        // DEFAULT
        KitGUI.kitGUI.setItem(0, DwarfLoadoutItems.dwarvenRunebladeEquipped);
        KitGUI.kitGUI.setItem(1, DwarfLoadoutItems.dwarvenShortbowEquipped);
        KitGUI.kitGUI.setItem(2, DwarfLoadoutItems.jimmyJuiceEquipped);
        KitGUI.kitGUI.setItem(3, DwarfLoadoutItems.dwarvenArmorEquipped);
        KitGUI.kitGUI.setItem(4, DwarfLoadoutItems.magicStone1Equipped);
        KitGUI.kitGUI.setItem(5, DwarfLoadoutItems.enchantedLamps1Equipped);
        KitGUI.kitGUI.setItem(6, DwarfLoadoutItems.wigglyWrenches1Equipped);
        KitGUI.kitGUI.setItem(7, DwarfLoadoutItems.stoneMason1Equipped);
        KitGUI.kitGUI.setItem(8, DwarfLoadoutItems.torchbearer1Equipped);


        // DIVIDERS
        KitGUI.kitGUI.setItem(9, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(10, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(11, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(12, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(13, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(14, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(15, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(16, DwarfLoadoutItems.divider);
        KitGUI.kitGUI.setItem(17, DwarfLoadoutItems.divider);

        KitGUI.kitGUI.setItem(48, DwarfLoadoutItems.leftArrow);
        KitGUI.kitGUI.setItem(50, DwarfLoadoutItems.rightArrow);
    }

    public static void loadoutMeleeItems() {
        KitGUI.kitMeleeGUI.setItem(0, DwarfLoadoutItems.dwarvenRuneblade);
    }
}
