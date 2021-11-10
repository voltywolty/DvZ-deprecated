package main.me.volt.dvz.utils;

import main.me.volt.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class ShrineBarManager {
    String name = "Shrine Power";
    public double health = 200.0D;

    private BossBar shrineBar = Bukkit.createBossBar(this.name + "Shrine Power" + " (" + (DvZ.plugin.shrineManager.currentShrine+1) + "/" + DvZ.plugin.shrineManager.shrines.size() + ")", BarColor.BLUE, BarStyle.SOLID);

    public ShrineBarManager(String name) {
        this.name = name.replace("Shrine Power", "");
    }

    public void sendBarToAllPlayers() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            shrineBar.setTitle(this.name + "Shrine Power" + " (" + (DvZ.plugin.shrineManager.currentShrine+1) + "/" + DvZ.plugin.shrineManager.shrines.size() + ")");
            shrineBar.addPlayer(players);
        }
    }

    public void sendBarToPlayer(Player p) {
        shrineBar.addPlayer(p);
    }

    public void removeBarForAllPlayers() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            shrineBar.removePlayer(players);
        }
    }

    public void changeBarHealth(double healthChange) {
        this.health += healthChange;
        if (this.health < 0) {
            this.health = 0;
        }

        if (this.health > 200) {
            this.health = 200;
        }

        //Bukkit.broadcastMessage("New Health: " + health + ", Progress: " + health / 200.0D);
        this.shrineBar.setProgress(this.health / 200D);
    }

    public void setBarName(String name) {
        this.name = name.replace("Shrine Power", "");
        this.shrineBar.setTitle(name);
    }
}