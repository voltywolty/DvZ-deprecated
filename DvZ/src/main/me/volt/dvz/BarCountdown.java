package main.me.volt.dvz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BarCountdown {
    public BossBar countdownBar = Bukkit.createBossBar(ChatColor.RED + "Waiting for more players (" + DvZ.plugin.minPlayers + " needed)", BarColor.BLUE, BarStyle.SOLID);
    boolean running = false;

    public void barWaitingForPlayers() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            countdownBar.setTitle(ChatColor.RED + "Waiting for more players (" + DvZ.plugin.minPlayers + " needed)");
            countdownBar.setProgress(0D);
            countdownBar.addPlayer(players);
            countdownBar.setVisible(true);
        }
    }

    public void countdownBar() {
        new BukkitRunnable() {
            int time = 0;
            int maxTime = 90;
            public void run() {
                int neededPlayers = DvZ.plugin.minPlayers;
                if (time >= maxTime || DvZ.plugin.minPlayers < neededPlayers) {
                    cancel();
                    removeBarFromAll();
                    return;
                }
                running = true;
                countdownBar.setTitle(ChatColor.BLUE + DvZ.plugin.mapName + " (" + ChatColor.AQUA + Bukkit.getOnlinePlayers().size() + " players" + ChatColor.BLUE + ")");
                countdownBar.setProgress(time / 90D);
                time++;
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

    public void stopBarTimer() {
        running = false;
    }
}
