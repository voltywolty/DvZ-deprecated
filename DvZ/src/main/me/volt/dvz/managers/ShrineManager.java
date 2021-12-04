package main.me.volt.dvz.managers;

import com.nisovin.magicspells.util.BoundingBox;
import main.me.volt.dvz.DvZ;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShrineManager {
    DvZ plugin;

    SpawnProtector spawnProtector;
    Location startingMobSpawn;

    int startingMobSpawnInvuln;
    int startingMobSpawnPulseRange;

    public List<Shrine> shrines = new ArrayList<>();

    public int currentShrine = 0;
    int pulseCounter = 0;

    List<String> shrineImmune = new ArrayList<>();

    public ShrineManager(DvZ plugin, Configuration config) {
        this.plugin = plugin;
        this.spawnProtector = new SpawnProtector();

        World world = Bukkit.getWorlds().get(0);
        String[] sp = config.getString("mob-spawn", "0,100,0,0").split(",");

        this.startingMobSpawn = new Location(world, Double.parseDouble(sp[0]), Double.parseDouble(sp[1]), Double.parseDouble(sp[2]), Float.parseFloat(sp[3]), 0.0F);
        this.startingMobSpawnInvuln = config.getInt("mob-spawn-invuln", 10);
        this.startingMobSpawnPulseRange = config.getInt("mob-spawn-pulse-range", 25);

        Set<String> keys = config.getConfigurationSection("shrines").getKeys(false);

        for (String key : keys) {
            Shrine shrine = new Shrine(config.getConfigurationSection("shrines." + key));
            this.shrines.add(shrine);
        }
    }

    public void startPulse() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable() {
            public void run() {
                ShrineManager.this.pulse();
            }
        }, 100L, 100L);
    }

    boolean running = true;
    public void decreaseShrineHealth() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable() {
            public void run() {
                String currentShrineName = getCurrentShrineName();

                if (!running)
                    return;

                if (plugin.shrineBarManager.health > 0 ) {
                    for (Player player : DvZ.plugin.monsters) {
                        if (playerNearShrineForCapture(player) && player.getGameMode().equals(GameMode.SURVIVAL)) {
                            DvZ.plugin.shrineBarManager.changeBarHealth(-shrines.get(currentShrine).monsterValue);
                        }
                    }
                    for (Player player : DvZ.plugin.dwarves) {
                        if (playerNearShrineForCapture(player) && player.getGameMode().equals(GameMode.SURVIVAL)) {
                            if (DvZ.plugin.shrineBarManager.health < 200 && DvZ.plugin.shrineBarManager.health > 0) {
                                DvZ.plugin.shrineBarManager.changeBarHealth(shrines.get(currentShrine).dwarfValue);
                            }
                        }
                    }
                }
                else if (DvZ.plugin.shrineBarManager.health <= 0) {
                    if (shrines.size() <= 0) {
                        running = false;
                        DvZ.plugin.endGame();
                    }
                    destroyCurrentShrine();

                    DvZ.plugin.shrineBarManager.health = 200.0D;
                    DvZ.plugin.shrinePower = 200.0D;
                    DvZ.plugin.shrineBarManager.setBarName(getCurrentShrineName() + " (" + (DvZ.plugin.shrineManager.currentShrine+1) + "/" + DvZ.plugin.shrineManager.shrines.size() + ")");

                    Bukkit.broadcastMessage(ChatColor.GOLD + "=================================================");
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "THE " + currentShrineName.toUpperCase() + " HAS FALLEN!");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "=================================================");
                }
            }
        }, 20, 20);
    }

    public boolean playerNearShrineForCapture(Player player) {
        return (this.shrines.get(this.currentShrine)).captureBoundingBox.contains(player);
    }

    public boolean playerNearShrineForPoints(Player player) {
        return (this.shrines.get(this.currentShrine)).pointsBoundingBox.contains(player);
    }

    public boolean destroyCurrentShrine() {
        (this.shrines.get(this.currentShrine)).destroy();

        if (this.currentShrine == this.shrines.size() - 1) {
            return true;
        }

        this.currentShrine++;
        this.pulseCounter = 0;

        return false;
    }

    public void respawnMob(Player player) {
        player.teleport(getMobSpawn());

        int protect = (this.currentShrine == 0) ? this.startingMobSpawnInvuln : (this.shrines.get(this.currentShrine - 1)).mobSpawnInvuln;
        if (protect > 0) {
            this.spawnProtector.protectFor(player, protect);
        }
    }

    public Location getMobSpawn() {
        Location loc;

        if (this.currentShrine == 0) {
            loc = this.startingMobSpawn.clone();
        }
        else {
            loc = (this.shrines.get(this.currentShrine - 1)).mobSpawn.clone();
        }
        Block block = loc.getBlock();
        while (block.getType() != Material.AIR) {
            loc.add(0.0D, 1.0D, 0.0D);
            block = loc.getBlock();
        }
        loc.add(0.5D, 0.5D, 0.5D);
        return loc;
    }

    public Location getCurrentShrine() {
        return (this.shrines.get(this.currentShrine)).center.clone();
    }

    public Location getCurrentShrineForTeleport() {
        Location loc = (this.shrines.get(this.currentShrine)).center.clone();
        Block block = loc.getBlock();
        while (block.getType() != Material.AIR) {
            loc.add(0.0D, 1.0D, 0.0D);
            block = loc.getBlock();
        }
        loc.add(0.5D, 0.5D, 0.5D);
        return loc;
    }

    public String getCurrentShrineName() {
        return (this.shrines.get(this.currentShrine)).name;
    }

    public Location getFinalShrine() {
        return (this.shrines.get(this.shrines.size() - 1)).center.clone();
    }

    public boolean atFirstShrine() {
        return (this.currentShrine == 0);
    }

    public boolean atFinalShrine() {
        return (this.currentShrine == this.shrines.size() - 1);
    }

    public double getDwarfValue() {
        return (this.shrines.get(this.currentShrine)).dwarfValue;
    }

    public double getMonsterValue() {
        return (this.shrines.get(this.currentShrine)).monsterValue;
    }

    public boolean shouldRestore() {
        return (this.shrines.get(this.currentShrine)).restore;
    }

    public double getMaxShrinePower() {
        return (this.shrines.get(this.currentShrine)).maxPower;
    }

    public void setShrineImmunity(Player player, boolean immune) {
        if (immune) {
            this.shrineImmune.add(player.getName());
        } else {
            this.shrineImmune.remove(player.getName());
        }
    }

    private void pulse() {
        for (int i = this.currentShrine + 1; i < this.shrines.size(); i++) {
            Shrine shrine = this.shrines.get(i);
            int rangeSq = shrine.pulseRange * shrine.pulseRange;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isValid() && !this.shrineImmune.contains(p.getPlayer()) && this.plugin.monsters.contains(p.getPlayer()) && shrine.center.distanceSquared(p.getLocation()) < rangeSq && p.getGameMode().equals((GameMode.SURVIVAL))) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                    p.damage(40);
                    p.sendMessage(ChatColor.RED + "You have been killed by the dwarven shrine! You must attack the weaker shrine first!");
                }
            }
        }
        int mobPulse = 0;
        if (this.pulseCounter++ <= 5) {
            mobPulse = 0;
        }
        else if (this.currentShrine == 0) {
            mobPulse = this.startingMobSpawnPulseRange;
        }
        else {
            mobPulse = (this.shrines.get(this.currentShrine - 1)).mobSpawnPulseRange;
        }
        if (mobPulse > 0) {
            mobPulse *= mobPulse;
            Location l = (this.currentShrine == 0) ? this.startingMobSpawn : (this.shrines.get(this.currentShrine - 1)).mobSpawn;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isValid() && this.plugin.dwarves.contains(p.getPlayer()) && l.distanceSquared(p.getLocation()) < mobPulse) {
                    if (p.hasPotionEffect(PotionEffectType.POISON)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3, true));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1, true));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1, true));
                        p.damage(10);
                        p.getInventory().setItemInMainHand(null);
                    }
                    else if (p.hasPotionEffect(PotionEffectType.WEAKNESS)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 3, true));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1, true));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1, true));
                    }
                    else if (p.hasPotionEffect(PotionEffectType.CONFUSION)) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1, true));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true));
                    }
                    else {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1, true));
                    }
                    p.removePotionEffect(PotionEffectType.ABSORPTION);
                    p.removePotionEffect(PotionEffectType.REGENERATION);
                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    p.sendMessage(ChatColor.RED + "You are too close to the mob spawn location!");
                }
            }
        }
    }

    class SpawnProtector implements Listener {
        Set<String> protect = new HashSet<>();

        public SpawnProtector() {
            Bukkit.getPluginManager().registerEvents(this, ShrineManager.this.plugin);
        }

        public void protectFor(final Player player, int duration) {
            this.protect.add(player.getName());

            new BukkitRunnable() {
                public void run() {
                    SpawnProtector.this.protect.remove(player.getPlayer());
                }
            }.runTaskLater(plugin, duration*20);
        }

        @EventHandler
        public void onDamage(EntityDamageEvent event) {
            if (event.getEntity() instanceof Player && this.protect.contains((event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onTeleport(PlayerTeleportEvent event) {
            this.protect.remove(event.getPlayer());
        }
    }

    public class Shrine {
        Location center;
        public Location mobSpawn;

        public String name;

        BoundingBox captureBoundingBox;
        BoundingBox pointsBoundingBox;

        int captureRadius;
        int pointsRadius;

        double dwarfValue;
        double monsterValue;

        boolean restore;

        double maxPower;

        double decay;
        int decayAfter;

        int pulseRange;
        int mobSpawnPulseRange;
        int mobSpawnInvuln;

        public Shrine(ConfigurationSection config) {
            String s = config.getString("coords");
            String[] coords = s.split(",");

            this.center = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]), Float.parseFloat(coords[3]), 0.0F);

            coords = config.getString("mob-spawn", s).split(",");

            this.mobSpawn = new Location(Bukkit.getWorlds().get(0), Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]), Float.parseFloat(coords[3]), 0.0F);
            this.name = config.getString("name", "Shrine");
            this.captureRadius = config.getInt("capture-radius", 10);
            this.captureBoundingBox = new BoundingBox(this.center, this.captureRadius);
            this.pointsRadius = config.getInt("points-radius", 500);
            this.pointsBoundingBox = new BoundingBox(this.center, this.pointsRadius);
            this.dwarfValue = config.getDouble("dwarf-value", 5.0D);
            this.monsterValue = config.getDouble("monster-value", 1.0D);
            this.restore = config.getBoolean("restore", true);
            this.maxPower = config.getInt("max-power", 100);
            this.decay = config.getDouble("decay", 0.0D);
            this.decayAfter = config.getInt("decay-after", 0);
            this.pulseRange = config.getInt("pulse-range", 20);
            this.mobSpawnPulseRange = config.getInt("mob-spawn-pulse-range", 0);
            this.mobSpawnInvuln = config.getInt("mob-spawn-invuln", 10);

            System.out.println("LOADED SHRINE: center=" + this.center + "; caprad=" + this.captureRadius + "; dwarfv=" + this.dwarfValue + "; monsterv=" + this.monsterValue + "; pulse=" + this.pulseRange);
        }

        public void destroy() {
            World world = this.center.getWorld();

            world.createExplosion(this.center, 3.0F);
            world.createExplosion(this.center.clone().add(3.0D, 0.0D, 0.0D), 3.0F);
            world.createExplosion(this.center.clone().add(0.0D, 0.0D, 3.0D), 3.0F);
            world.createExplosion(this.center.clone().add(-3.0D, 0.0D, 0.0D), 3.0F);
            world.createExplosion(this.center.clone().add(0.0D, 0.0D, -3.0D), 3.0F);
            world.createExplosion(this.center.clone().add(3.0D, 0.0D, 3.0D), 3.0F);
            world.createExplosion(this.center.clone().add(3.0D, 0.0D, -3.0D), 3.0F);
            world.createExplosion(this.center.clone().add(-3.0D, 0.0D, 3.0D), 3.0F);
            world.createExplosion(this.center.clone().add(-3.0D, 0.0D, -3.0D), 3.0F);

            for (Player player : plugin.monsters) {
                if (player.isValid() && !player.isDead()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 3, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1, false));
                }
            }

            for (Player player : plugin.dwarves) {
                if (player.isValid() && !player.isDead()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1, false));
                }
            }

            for (int x = this.center.getBlockX() - 10; x <= this.center.getBlockX() + 10; x++) {
                for (int y = this.center.getBlockY() - 10; y <= this.center.getBlockY() + 10; y++) {
                    for (int z = this.center.getBlockZ() - 10; z <= this.center.getBlockZ() + 10; z++) {
                        Block block = this.center.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == Material.END_PORTAL_FRAME) {
                            block.setType(Material.AIR);

                            FallingBlock fallBlock = block.getWorld().spawnFallingBlock(block.getLocation(), Material.END_PORTAL_FRAME.createBlockData());

                            fallBlock.setDropItem(false);
                            fallBlock.setVelocity(new Vector(ShrineManager.this.plugin.random.nextDouble() * 10.0D - 5.0D, ShrineManager.this.plugin.random.nextDouble() * 5.0D, ShrineManager.this.plugin.random.nextDouble() * 10.0D - 5.0D));
                        }
                        else if (block.getType() == Material.BEACON) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}
