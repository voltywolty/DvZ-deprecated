package main.me.volt.dvz.gui;

import main.me.volt.dvz.items.cosmetic.HatItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class HatGUI {
    public static Inventory hatGUI = Bukkit.createInventory(null, 18, "Select a Hat");
    public static HatItems hats;

    public HatGUI() {

    }

    public static void setHats() {
        hatGUI.setItem(0, hats.pharaohHat);
        hatGUI.setItem(1, hats.goggles);
        hatGUI.setItem(2, hats.warriorCap);
        hatGUI.setItem(3, hats.dragonsBreathHat);
        hatGUI.setItem(4, hats.wolfHunterHat);
        hatGUI.setItem(5, hats.jimmyCap);
        hatGUI.setItem(6, hats.dwarvenBeard);
        hatGUI.setItem(7, hats.santaHat);
    }
}
