package main.me.volt.dvz.managers;

import com.nisovin.magicspells.util.BoundingBox;
import main.me.volt.dvz.DvZ;

import main.me.volt.dvz.utils.ShrineBarManager;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShrineManager {
    DvZ plugin;

    SpawnProtector spawnProtector;
    Location startingMobSpawn;
    ShrineBarManager shrineBarManager = new ShrineBarManager("Shrine Power");

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
        Bukkit.getScheduler().runTaskTimer((Plugin) this.plugin, new Runnable() {
            public void run() {
                ShrineManager.this.pulse();
            }
        }, 100L, 100L);
    }

    boolean running = true;
    public void decreaseShrineHealth() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable() {
            public void run() {
                if (!running)
                    return;

                for (Player player : DvZ.plugin.monsters) {
                    if (playerNearShrineForCapture(player)) {
                        shrineBarManager.changeBarHealth(-(float) shrines.get(currentShrine).monsterValue);

                        // TEST TEXT
                        Bukkit.broadcastMessage("Shrine taking damage. Health is at: " + shrineBarManager.health);
                    }
                }
                for (Player player : DvZ.plugin.dwarves) {
                    if (playerNearShrineForCapture(player)) {
                        if (shrineBarManager.health != 200.0) {
                            shrineBarManager.changeBarHealth((float) shrines.get(currentShrine).dwarfValue);
                        }
                        // TEST TEXT
                        Bukkit.broadcastMessage("Shrine being healed to: " + shrineBarManager.health);
                    }
                }

                if (shrineBarManager.health <= 0) {
                    if (shrines.size() <= 0) {
                        DvZ.plugin.endGame();
                        running = false;
                    }
                    ShrineManager.this.destroyCurrentShrine();
                    shrineBarManager.health = 200.0D;
                    //shrineBarManager.setBarName();
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
                if (p.isValid() && !this.shrineImmune.contains(p.getPlayer()) && this.plugin.monsters.contains(p.getPlayer()) && shrine.center.distanceSquared(p.getLocation()) < rangeSq) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                    p.setHealth(0.0D);
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
            Bukkit.getPluginManager().registerEvents(this, (Plugin)ShrineManager.this.plugin);
        }

        public void protectFor(final Player player, int duration) {
            this.protect.add(player.getName());

            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) ShrineManager.this.plugin, new Runnable() {
                public void run() {
                    SpawnProtector.this.protect.remove(player.getPlayer());
                }
            }, (duration * 20));
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

    class Shrine {
        Location center;
        Location mobSpawn;

        String name;

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

            for (int x = this.center.getBlockX() - 10; x <= this.center.getBlockX() + 10; x++) {
                for (int y = this.center.getBlockY() - 10; y <= this.center.getBlockY() + 10; y++) {
                    for (int z = this.center.getBlockZ() - 10; z <= this.center.getBlockZ() + 10; z++) {
                        Block block = this.center.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == Material.END_PORTAL_FRAME) {
                            block.setType(Material.AIR);

                            FallingBlock fallBlock = block.getWorld().spawnFallingBlock(block.getLocation(), Material.END_PORTAL_FRAME, (byte) 0);

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
