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
        classGUI.setItem(0, DwarfItems.warriorKit);
        classGUI.setItem(1, DwarfItems.paladinKit);
        classGUI.setItem(2, DwarfItems.rangerKit);

        player.openInventory(classGUI);
        return true;
    }
}
