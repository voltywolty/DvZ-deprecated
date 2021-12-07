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
    public Set<Player> plagued = new HashSet<>();

    public PlagueEvent(DvZ plugin) {
        super(plugin);
    }

    public String getName() {
        return "plague";
    }

    boolean done = false;
    public void run() {
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p);
            p.sendMessage(ChatColor.RED + "You feel other around you beginning to feel a bit sick...");
        }

        int shuffleCount = this.random.nextInt(5) + 1;
        for (int i = 0; i < shuffleCount; i++) {
            Collections.shuffle(players, this.random);
        }

        int monsterCount = this.plugin.monsters.size();
        int monstersNeeded = 4;

        if (Bukkit.getOnlinePlayers().size() <= 7) {
            monstersNeeded = Bukkit.getOnlinePlayers().size() / 2;
        }

        System.out.println("Monster Count: " + monsterCount + " | Monsters Wanted: " + monstersNeeded);

        if (monsterCount < monstersNeeded) {
            int c = 0;

            if (!done) {
                System.out.println("Getting more plague victims...");
                for (int j = 0; j < players.size(); j++) {
                    Player player = players.get(j);

                    if (player.hasPermission("dvz.immune")) {
                        System.out.println("Tried to plague " + player.getName() + ", but that player is immune.");
                    }
                    else if (!this.plugin.dwarves.contains(player)) {
                        System.out.println("Tried to plague" + player.getName() + ", but that player is not a dwarf.");
                    }
                    else if (this.plagued.contains(player)) {
                        System.out.println("Tried to plague" + player.getName() + ", but that player already has the plague.");
                    }
                    else {
                        System.out.println("Player " + player.getName() + " has been given the plague!");
                        plaguePlayer(player);
                        c++;
                        if (c++ >= monstersNeeded) {
                            System.out.println("Plague completed " + c);
                            done = true;
                            break;
                        }
                    }
                }
            }
        }
        this.plugin.releaseMonsters();
    }

    private void plaguePlayer(Player player) {
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

    public void setDwarf(Player player) {
        this.plagued.remove(player);
    }

    public boolean deathCountsAsKillForMonsterTeam(Player player) {
        return !this.plagued.contains(player);
    }

    public GameEvent.DamageResult checkDamage(Player attacker, Player defender) {
        if (this.plugin.dwarves.contains(attacker) && this.plugin.dwarves.contains(defender) && this.plagued.contains(defender))
            return GameEvent.DamageResult.ALLOW;
        return GameEvent.DamageResult.NORMAL;
    }
}
