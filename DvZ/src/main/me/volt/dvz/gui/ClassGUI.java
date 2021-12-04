package main.me.volt.dvz.gui;

import main.me.volt.dvz.items.DwarfItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ClassGUI {
    public static Inventory classGUI = Bukkit.createInventory(null, 9, "Select a Kit");

    public ClassGUI() {

    }

    public static boolean openGUI(Player player) {
        //classGUI.setItem(0, DwarfItems.pharaohHat);
        //classGUI.setItem(1, DwarfItems.goggles);
        classGUI.setItem(2, DwarfItems.rangerClass);
        //classGUI.setItem(3, DwarfItems.dragonsBreathHat);
        //classGUI.setItem(4, DwarfItems.wolfHunterHat);

        player.openInventory(classGUI);
        return true;
    }
}
