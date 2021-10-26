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
    public Set<String> plagued = new HashSet<>();

    public PlagueEvent(DvZ plugin) {
        super(plugin);
    }

    public String getName() {
        return "plague";
    }

    public void run() {
        List<Player> players = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            players.add(p);
        }

        int shuffleCount = this.random.nextInt(5) + 1;
        for (int i = 0; i < shuffleCount; i++) {
            Collections.shuffle(players, this.random);
        }

        int monsterCount = getMonstersNeeded();
        System.out.println("Monster Count: " + monsterCount);

        if (monsterCount > 0) {
            int c = 0;
            boolean done = false;

            if (!done) {
                System.out.println("Getting more plague victims...");

                for (int j = 0; j < players.size(); j++) {
                    Player player = players.get(j);

                    if (player.hasPermission("game.ignore") || player.hasPermission("dvz.immune")) {
                        System.out.println("Tried to plague " + player.getName() + ", but that player is immune!");
                    }
                    else if (!this.plugin.dwarves.contains(player.getName())) {
                        System.out.println("Tried to plague " + player.getName() + ", but that player is not a dwarf!");
                    }
                    else if (this.plagued.contains(player.getName())) {
                        System.out.println("Tried to plague " + player.getName() + ", but that player already has the plague!");
                    }
                    else {
                        System.out.println("Player " + player.getName() + " has been given the plague!");
                        plaguePlayer(player);

                        if (c++ >= monsterCount) {
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

    private void plaguePlayer(final Player player) {
        this.plagued.add(player.getName());

        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100000, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100000, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100000, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING   , 100000, 2));

        if (player.getHealth() > 20.0D) {
            player.setHealth(20.0D);
        }
        MagicSpells.getManaHandler().addMana(player, -1000, ManaChangeReason.OTHER);
        player.sendMessage(ChatColor.DARK_RED + "You have contracted the " + ChatColor.GREEN + "Zombie Plague" + ChatColor.DARK_RED + "!");

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
                if (player.isValid() && PlagueEvent.this.plugin.dwarves.contains(player.getName())) {
                    player.setHealth(0.0D);
                }
            }
        }, 600L);
    }

    public void setDwarf(Player paramPlayer) {
        this.plagued.remove(paramPlayer.getName());
    }

    public boolean deathCountsAsKillForMonsterTeam(Player paramPlayer) {
        return !this.plagued.contains(paramPlayer.getName());
    }

    public DamageResult checkDamage(Player attacker, Player defender) {
        if (this.plugin.dwarves.contains(attacker.getName()) && this.plugin.dwarves.contains(defender.getName()) && this.plagued.contains(defender.getName())) {
            return DamageResult.ALLOW;
        }
        return DamageResult.NORMAL;
    }
}
