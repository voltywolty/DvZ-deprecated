package main.me.volt.dvz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BarCountdown {
    public BossBar countdownBar = Bukkit.createBossBar(ChatColor.RED + "Waiting for more players...", BarColor.BLUE, BarStyle.SOLID);

    public void barWaitingForPlayers() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            countdownBar.setTitle(ChatColor.RED + "Waiting for more players...");
            countdownBar.setProgress(0D);
            countdownBar.addPlayer(players);
            countdownBar.setVisible(true);
        }
    }

    public void countdownBar() {
        new BukkitRunnable() {
            public void run() {
                int neededPlayers = DvZ.plugin.minPlayers;
                if ((DvZ.plugin.autoStartTime -= 1) <= 0 || DvZ.plugin.minPlayers < neededPlayers) {
                    cancel();
                    removeBarFromAll();
                }
                else {
                    countdownBar.setTitle(ChatColor.BLUE + "Charging shrines... (" + ChatColor.AQUA + DvZ.plugin.autoStartTime + " seconds" + ChatColor.BLUE + ")");
                    countdownBar.setProgress(DvZ.plugin.autoStartTime / 30D);
                }
            }
        }.runTaskTimer(DvZ.plugin, 0, 20);
    }

    public void addBarToPlayer(Player p) {
        countdownBar.addPlayer(p);
        countdownBar.setVisible(true);
    }

    public void removeBarFromPlayer(Player p) {
        countdownBar.removePlayer(p);
        countdownBar.setVisible(false);
    }

    public void removeBarFromAll() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            countdownBar.removePlayer(players);
            countdownBar.setVisible(false);
        }
    }
}
