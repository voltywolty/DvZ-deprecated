package main.me.volt.dvz.utils;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.ManaChangeEvent;
import com.nisovin.magicspells.mana.ManaHandler;
import com.nisovin.magicspells.util.IntMap;
import main.me.volt.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bleeder implements Listener {
    final int bleedMana = 500;

    DvZ plugin;
    ManaHandler manaHandler;

    Map<String, Player> bleeding = new HashMap<>();
    IntMap<String> mana = new IntMap<>();

    public Bleeder(DvZ plugin) {
        this.plugin = plugin;
        this.manaHandler = MagicSpells.getManaHandler();

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                Bleeder.this.bleed();
            }
        }, 8L, 8L);
    }

    private void bleed() {
        List<String> toRemove = new ArrayList<>();
        for (Player player : this.bleeding.values()) {
            int pmana = this.mana.get(player.getName());
            if (pmana > 0 && player.isValid() && pmana < bleedMana && this.plugin.dwarves.contains(player)) {
                int c = (500 - pmana) / 33;
                if (pmana < 200) {
                    c *= 2;
                }
                player.spawnParticle(Particle.REDSTONE, player.getLocation().add(0.0D, 1.0D, 0.0D).getY(), 0.2F, 0.2F, 15);
                continue;
            }
            toRemove.add(player.getName());
        }
        for (String name : toRemove) {
            this.bleeding.remove(name);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onManaChange(ManaChangeEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        if (event.getNewAmount() < 500) {
            this.mana.put(name, event.getNewAmount());
            if (this.plugin.dwarves.contains(name)) {
                this.bleeding.put(name, player);
            }
        }
        else {
            this.bleeding.remove(name);
        }
    }
}
