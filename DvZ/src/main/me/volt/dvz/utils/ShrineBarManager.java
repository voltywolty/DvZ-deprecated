package main.me.volt.dvz.utils;

import main.me.volt.dvz.DvZ;
import main.me.volt.dvz.managers.ShrineManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class ShrineBarManager {
    String name = "";
    public double health = 200.0D;

    private BossBar shrineBar = Bukkit.createBossBar(this.name + " (" + (DvZ.plugin.shrineManager.currentShrine+1) + "/" + DvZ.plugin.shrineManager.shrines.size() + ")", BarColor.BLUE, BarStyle.SOLID);

    public ShrineBarManager(String name) {
        this.name = name;
    }

    public void sendBarToAllPlayers() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            shrineBar.setTitle(this.name + " (" + (DvZ.plugin.shrineManager.currentShrine+1) + "/" + DvZ.plugin.shrineManager.shrines.size() + ")");
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

        this.shrineBar.setProgress(this.health / 200D);
    }

    public void setBarName(String name) {
        this.name = name;
        this.shrineBar.setTitle(name);
    }
}