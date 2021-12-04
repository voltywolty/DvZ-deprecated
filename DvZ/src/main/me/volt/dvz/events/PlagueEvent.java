package main.me.volt.dvz.events;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaChangeReason;
import main.me.volt.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlagueEvent extends GameEvent {
    private Set<Player> plagued = new HashSet<>();

    public PlagueEvent(DvZ plugin) {
        super(plugin);
    }

    public String getName() {
        return "plague";
    }

    public void run() {
        List<UUID> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p.getUniqueId());
            p.sendMessage(ChatColor.RED + "You feel other around you beginning to feel a bit sick...");
        }

        int monsterCount = this.plugin.monsters.size();
        int monstersWanted = 4;

        if (Bukkit.getOnlinePlayers().size() <= 7) {
            monstersWanted = Bukkit.getOnlinePlayers().size() / 2;
        }
        System.out.println("Monster Count: " + monsterCount + " | Monsters Wanted: " + monstersWanted);

        if (monsterCount < monstersWanted) {
            for (int i = monsterCount; i < monstersWanted; i++) {
                System.out.println("Monsters not full. Finding more victims.");

                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player == null)
                        continue;

                    if (!this.plugin.dwarves.contains(player.getPlayer())) {
                        System.out.println("Tried to infect " + player.getName() + ", but that player is not a dwarf!");
                        continue;
                    }
                    if (this.plagued.contains(player.getPlayer())) {
                        System.out.println("Tried to infect " + player.getName() + ", but that player already has the plague!");
                    }

                    System.out.println("Player " + player.getName() + " has been given the plague!");
                    plaguePlayer(player);
                    continue;
                }
            }
        }
        this.plugin.releaseMonsters();
    }

    private void plaguePlayer(final Player player) {
        this.plagued.add(player.getPlayer());

        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 2));

        if (player.getHealth() > 20.0D) {
            player.setHealth(20.0D);
        }
        MagicSpells.getManaHandler().addMana(player, -1000, ManaChangeReason.OTHER);

        player.sendMessage(ChatColor.DARK_RED + "You have contracted the " + ChatColor.GREEN + "Zombie Plague" + ChatColor.DARK_RED + "!");
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
                if (player.isValid() && PlagueEvent.this.plugin.dwarves.contains(player.getPlayer())) {
                    player.setHealth(0.0D);
                }
            }
        }, 600L);
    }

    public void setDwarf(Player paramPlayer) {
        this.plagued.remove(paramPlayer);
    }

    public boolean deathCountsAsKillForMonsterTeam(Player paramPlayer) {
        return !this.plagued.contains(paramPlayer);
    }

    public DamageResult checkDamage(Player attacker, Player defender) {
        if (this.plugin.dwarves.contains(attacker) && this.plugin.dwarves.contains(defender) && this.plagued.contains(defender)) {
            return DamageResult.ALLOW;
        }
        return DamageResult.NORMAL;
    }
}
