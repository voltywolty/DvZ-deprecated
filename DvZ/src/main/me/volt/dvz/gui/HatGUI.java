package main.me.volt.dvz.gui;

import main.me.volt.dvz.items.DwarfItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HatGUI {
    public static Inventory hatGUI = Bukkit.createInventory(null, 9, "Hat Selector");

    public HatGUI() {

    }

    public static boolean openGUI(Player player) {
        hatGUI.setItem(0, DwarfItems.pharaohHat);
        hatGUI.setItem(1, DwarfItems.goggles);
        hatGUI.setItem(2, DwarfItems.warriorCap);
        hatGUI.setItem(3, DwarfItems.dragonsBreathHat);
        hatGUI.setItem(4, DwarfItems.wolfHunterHat);
        hatGUI.setItem(5, DwarfItems.dwarvenCap);
        hatGUI.setItem(6, DwarfItems.dwarvenBeard);
        hatGUI.setItem(7, DwarfItems.santaHat);
        hatGUI.setItem(8, DwarfItems.clearHat);

        player.openInventory(hatGUI);
        return true;
    }
}
