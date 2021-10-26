package main.me.volt.dvz.events;

import main.me.volt.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Random;

public abstract class GameEvent {
    protected DvZ plugin;

    protected Random random;

    public GameEvent(DvZ plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public abstract String getName();

    public abstract void run();
    public abstract void setDwarf(Player paramPlayer);

    public abstract boolean deathCountsAsKillForMonsterTeam(Player paramPlayer);
    public abstract DamageResult checkDamage(Player paramPlayer1, Player paramPlayer2);

    protected int getMonstersNeeded() {
        int needed = Math.round((Bukkit.getOnlinePlayers()).toArray().length * this.plugin.percentMonsters / 100.0F);

        for (Player player : Bukkit.getOnlinePlayers()) {
            needed--;
            if ((player.isDead()) || this.plugin.monsters.contains(player.getName()) && needed == 0) {
                break;
            }
        }
        return (needed > 0) ? needed : 0;
    }

    public enum DamageResult {
        NORMAL, ALLOW, DENY;
    }
}
