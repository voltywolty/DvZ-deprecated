package main.me.volt.dvz.events;

import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.util.IntMap;
import main.me.volt.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class RageEvent extends GameEvent {
    private Set<Player> diedDuringEvent = new HashSet<>();
    private IntMap<Player> participation = new IntMap<>();

    private boolean eventActive = false;

    RageEventListener listener;
    BukkitTask failSafeTask;

    public RageEvent(DvZ plugin) {
        super(plugin);
    }

    public String getName() {
        return "rage";
    }

    int monsterCount = this.plugin.monsters.size();
    int monstersNeeded = 4;
    public void run() {
        if (Bukkit.getOnlinePlayers().size() <= 7) {
            monstersNeeded = Bukkit.getOnlinePlayers().size() / 2;
        }

        if (monsterCount > monstersNeeded) {
            this.plugin.releaseMonsters();
            return;
        }

        this.plugin.getLogger().info("[Rage Event] Starting soon...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isValid() && this.plugin.dwarves.contains(player)) {
                player.sendMessage(ChatColor.RED + "You begin to feel a bit " + ChatColor.DARK_RED + "angry" + ChatColor.RED + "...");
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            public void run() {
                RageEvent.this.start();
            }
        }, 200L);
    }

    private void start() {
        this.plugin.getLogger().info("[Rage Event] Starting");
        this.eventActive = true;
        this.listener = new RageEventListener();

        Bukkit.getPluginManager().registerEvents(this.listener, this.plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isValid() && this.plugin.dwarves.contains(player)) {
                player.sendMessage(ChatColor.DARK_GRAY + "=================================================");
                player.sendMessage(ChatColor.RED + "YOU ARE FILLED WITH " + ChatColor.DARK_RED + "DRAGON RAGE" + ChatColor.RED + "!");
                player.sendMessage(ChatColor.DARK_PURPLE + "Take out your anger on your fellow dwarves!");
                player.sendMessage(ChatColor.DARK_PURPLE + "If you don't vent your anger, you may suffer a " + ChatColor.RED + "HEART ATTACK" + ChatColor.DARK_PURPLE + "!");
                player.sendMessage(ChatColor.DARK_GRAY + "=================================================");

                this.plugin.dwarfTeam.removeEntry(player.getName());

                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3.0F, 1.0F);
            }
        }
        this.failSafeTask = Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
            public void run() {
                if (RageEvent.this.eventActive) {
                    int needed = RageEvent.this.monstersNeeded;

                    if (RageEvent.this.monsterCount > needed) {
                        RageEvent.this.plugin.getLogger().info("[Rage Event] Activating Failsafe");
                        List<Player> highBloodPressure = new ArrayList<>();

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isValid() && RageEvent.this.plugin.dwarves.contains(player)) {
                                highBloodPressure.add(player);
                            }
                        }
                        Collections.sort(highBloodPressure, new Comparator<Player>() {
                            public int compare(Player p1, Player p2) {
                                int val1 = RageEvent.this.participation.get(p1);
                                int val2 = RageEvent.this.participation.get(p2);

                                if (val1 < val2)
                                    return -1;
                                if (val1 > val2)
                                    return 1;
                                return 0;
                            }
                        });
                        for (int i = 0; i < needed && i < highBloodPressure.size(); i++) {
                            Player player = highBloodPressure.get(i);
                            player.sendMessage(ChatColor.RED + "You bottled up your " + ChatColor.DARK_RED + "DRAGON RAGE" + ChatColor.RED + "and have suffered a heart attack.");
                            player.setHealth(0.0D);
                            RageEvent.this.plugin.getLogger().info("Killed " + player.getName());
                        }
                    }
                    RageEvent.this.end();
                }
            }
        }, 1200L);
    }

    private void end() {
        this.eventActive = false;
        HandlerList.unregisterAll(this.listener);

        if (this.failSafeTask != null)
            this.failSafeTask.cancel();
        this.plugin.releaseMonsters();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isValid() && this.plugin.dwarves.contains(player)) {
                player.sendMessage(ChatColor.GREEN + "Your dragon rage has subsided.");
                player.setHealth(player.getHealth());
                this.plugin.dwarfTeam.removeEntry(player.getName());
            }
        }
        this.plugin.getLogger().info("[Rage Event] Completed");
    }

    public void setDwarf(Player player) {
        this.diedDuringEvent.remove(player);
    }

    public boolean deathCountsAsKillForMonsterTeam(Player player) {
        return !this.eventActive;
    }

    public GameEvent.DamageResult checkDamage(Player attacker, Player defender) {
        if (this.eventActive &&
                this.plugin.dwarves.contains(attacker) && this.plugin.dwarves.contains(defender))
            return GameEvent.DamageResult.ALLOW;
        return GameEvent.DamageResult.NORMAL;
    }

    class RageEventListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
        public void onDamage(EntityDamageByEntityEvent event) {
            Player damaged = null;

            if (event.getEntity() instanceof Player) {
                damaged = (Player) event.getEntity();
            }
            else if (event.getEntity() instanceof Projectile && ((Projectile) event.getEntity()).getShooter() instanceof Player) {
                damaged = (Player)((Projectile) event.getEntity()).getShooter();
            }
            Player damager = null;
            if (event.getDamager() instanceof Player) {
                damager = (Player)event.getDamager();
            } else if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) {
                damager = (Player)((Projectile)event.getDamager()).getShooter();
            }
            if (damaged != null && damager != null && RageEvent.this.plugin.dwarves.contains(damager) && RageEvent.this.plugin.dwarves.contains(damaged)) {
                RageEvent.this.participation.increment(damager, (int)Math.ceil(event.getDamage() * 2.0D));
                RageEvent.this.participation.increment(damaged, (int)Math.ceil(event.getDamage()));
            }
        }

        @EventHandler
        public void onSpellCast(SpellCastEvent event) {
            if (RageEvent.this.plugin.dwarves.contains(event.getCaster().getName())) {
                event.setCancelled(true);
                if (!(event.getSpell() instanceof com.nisovin.magicspells.spells.PassiveSpell))
                    event.getCaster().sendMessage(ChatColor.RED + "You are too " + ChatColor.DARK_RED + "ANGRY" + ChatColor.RED + " to do that!");
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onDeath(PlayerDeathEvent event) {
            RageEvent.this.diedDuringEvent.add(event.getEntity());
            if (RageEvent.this.monsterCount > monstersNeeded)
                RageEvent.this.end();
        }
    }
}
