package main.me.volt.dvz.listeners;

import main.me.volt.dvz.DvZ;
import main.me.volt.dvz.trackers.LightBreakTracker;
import me.volt.main.mcevolved.MCEvolved;

import com.nisovin.magicspells.events.SpellTargetEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

import java.util.Iterator;

public class GameListener implements Listener {
    DvZ plugin;
    LightBreakTracker lightBreakTracker;

    public GameListener(DvZ plugin) {
        this.plugin = plugin;
        this.lightBreakTracker = new LightBreakTracker();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        final Player p = event.getEntity();
        event.getDrops().clear();
        event.setDroppedExp(0);

        boolean isDwarf = this.plugin.dwarves.contains(p.getPlayer());
        boolean isMonster = this.plugin.monsters.contains(p.getPlayer());

        if (isDwarf) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    GameListener.this.plugin.dwarves.remove(p.getPlayer());
                    int s = GameListener.this.plugin.remainingDwarvesScore.getScore() - 1;

                    if (s < 0) {
                        s = 0;
                    }
                    GameListener.this.plugin.remainingDwarvesScore.setScore(s);

                    if (s == 0 && GameListener.this.plugin.gameRunning) {
                        GameListener.this.plugin.recount();
                    }
                    plugin.updateScoreboards();
                }
            }, 5L);

            for (Player players : Bukkit.getOnlinePlayers()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> players.sendTitle(ChatColor.AQUA + p.getName() + ChatColor.RED + " perished!", "", 10, 20, 10), 20L);
            }
        }
        else if (isMonster) {
            this.plugin.monsterKills = this.plugin.monsterKillsScore.getScore() + 1;
            this.plugin.monsterKillsScore.setScore(this.plugin.monsterKills);

            this.plugin.updateScoreboards();
        }

        if (this.plugin.gameRunning) {
            this.plugin.shrineManager.setShrineImmunity(p, false);
            this.plugin.updateScoreboards();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);
        event.getDrops().clear();

        EntityType type = event.getEntityType();
        if (this.plugin.gameRunning && (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.CREEPER)) {
            int monsterKills = this.plugin.monsterKillsScore.getScore() + 1;
            this.plugin.monsterKillsScore.setScore(monsterKills);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Healing Ale")) {
            event.setCancelled(true);
        }
    }

    public int neededPlayers;
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        neededPlayers++;
        if (this.plugin.autoStartTime > 0 && !this.plugin.gameRunning && neededPlayers == this.plugin.minPlayers) {
            MCEvolved.startCountdown(this.plugin.autoStartTime, this.plugin.minPlayers);
        }

        if (this.plugin.gameRunning) {
            this.plugin.initializeScoreboard();
            this.plugin.updateScoreboards();


            final Player player = event.getPlayer();

            if (this.plugin.volatileCode != null) {
                this.plugin.volatileCode.sendBarToPlayer(event.getPlayer());
            }

            if (player.hasPermission("game.ignore")) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    if (!player.isDead()) {
                        if (GameListener.this.plugin.monstersReleased) {
                            if (!player.hasPermission("dvz.nojoindeath") && (!GameListener.this.plugin.dwarves.contains(player.getPlayer()) || GameListener.this.plugin.killDwarvesOnJoin)) {
                                player.getInventory().clear();
                                player.getInventory().setArmorContents(null);

                                Bukkit.getScheduler().scheduleSyncDelayedTask(GameListener.this.plugin, new Runnable() {
                                    public void run() {
                                        player.setHealth(0.0D);
                                    }
                                }, 5L);
                            }
                            else if (player.hasPermission("dvz.nojoindeath") && !GameListener.this.plugin.dwarves.contains(player.getPlayer()) && !GameListener.this.plugin.monsters.contains(player.getName())) {
                                GameListener.this.plugin.setAsDwarf(player);
                            }
                        }
                        else if (!GameListener.this.plugin.dwarves.contains(player.getPlayer()) && !GameListener.this.plugin.monsters.contains(player.getPlayer())) {
                            GameListener.this.plugin.setAsDwarf(player);
                        }
                    }
                }
            }, 5L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        neededPlayers--;

        if (neededPlayers != this.plugin.minPlayers) {
            MCEvolved.stopCountdown();
        }
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        if (this.plugin.gameRunning) {
            if (event.getPlayer().hasPermission("game.ignore")) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    Player player = event.getPlayer();

                    GameListener.this.plugin.dwarves.remove(player.getPlayer());
                    GameListener.this.plugin.monsters.add(player.getPlayer());

                    if (GameListener.this.plugin.volatileCode != null) {
                        //GameListener.this.plugin.volatileCode.sendBarToPlayer(player);
                    }
                }
            }, 1L);

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    Player player = event.getPlayer();
                    GameListener.this.plugin.becomeMonsterSpell.cast(player, 1.0F, null);
                }
            }, 10L);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.plugin.dwarves.contains(event.getPlayer().getName()) && event.getBlock().getType() == Material.COBBLESTONE) {
            int count = 0;

            Block block = event.getBlock().getRelative(BlockFace.DOWN);
            while(block.getType() == Material.AIR && count < 10) {
                block = block.getRelative(BlockFace.DOWN);
                count++;
            }

            if (count == 10) {
                event.setCancelled(true);
            }
        }

        if (event.getBlock().getType() == Material.ZOMBIE_HEAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!this.plugin.gameRunning) {
            event.setCancelled(true);
            return;
        }
        else {
            event.setCancelled(false);
        }

        if (event.getEntity() instanceof Zombie && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }

        if (this.plugin.monsters.contains(event.getEntity().getName()) && event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.DROWNING || event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage2(EntityDamageByEntityEvent event) {
        LivingEntity livingEntity = null;

        if (event.getDamager() instanceof Player && this.plugin.dwarves.contains(event.getDamager().getName()))
            event.setCancelled(true);

        if (event.getEntity().getType() == EntityType.ZOMBIE && event.getDamager() instanceof Player && this.plugin.monsters.contains(event.getDamager().getName()))
            event.setCancelled(true);

        if (!(event.getEntity() instanceof Player))
            return;

        Player attacked = (Player)event.getEntity();
        Entity e = event.getDamager();

        if (e instanceof Projectile && ((Projectile)e).getShooter() instanceof Player)
            livingEntity = (LivingEntity)((Projectile)e).getShooter();

        if (!(livingEntity instanceof Player))
            return;

        Player attacker = (Player)livingEntity;

        if (this.plugin.dwarves.contains(attacked.getPlayer()) && this.plugin.dwarves.contains(attacker.getPlayer())) {
            event.setCancelled(true);
        }
        else if (this.plugin.monsters.contains(attacked.getPlayer()) && this.plugin.monsters.contains(attacker.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        event.getEntity().remove();
    }

    @EventHandler
    public void onSpellTarget(SpellTargetEvent event) {
        Player player = (Player)event.getCaster();

        if (player.hasPermission("game.ignore")) {
            return;
        }

        if (!(event.getTarget() instanceof Player)) {
            return;
        }

        Player target = (Player)event.getTarget();

        if (this.plugin.dwarves.contains(player.getPlayer())) {
            if (event.getSpell().isBeneficial()) {
                if (!this.plugin.dwarves.contains(target.getPlayer())) {
                    event.setCancelled(true);
                }
            }
            else if (!this.plugin.monsters.contains(target.getPlayer())) {
                event.setCancelled(true);
            }
        }
        else if (this.plugin.monsters.contains(player.getPlayer())) {
            if (event.getSpell().isBeneficial()) {
                event.setCancelled(true);
            }
            else if (!this.plugin.dwarves.contains(target.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        EntityType type = event.getRightClicked().getType();
        if (type == EntityType.MINECART_CHEST || type == EntityType.MINECART_HOPPER || type == EntityType.MINECART_FURNACE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE || event.getEntityType() == EntityType.SKELETON) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (this.plugin.monsters.contains(event.getPlayer().getPlayer())) {
            event.setCancelled(true);
        }
        else {
            Material mat = event.getItemDrop().getItemStack().getType();

            if (mat == Material.DIAMOND_HELMET || mat == Material.DIAMOND_CHESTPLATE || mat == Material.DIAMOND_LEGGINGS || mat == Material.DIAMOND_BOOTS) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHungerBar(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
        (event.getEntity()).setSaturation(20.0F);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
            event.setCancelled(true);
        }
        else if (event.getSlotType() == InventoryType.SlotType.ARMOR && this.plugin.monsters.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setExpToDrop(0);
        if (!event.getPlayer().hasPermission("game.ignore")) {
            Material type = event.getBlock().getType();
            if (type == Material.IRON_BLOCK || type == Material.DIAMOND_BLOCK || type == Material.GLASS || type == Material.PISTON_HEAD || type == Material.PISTON || type == Material.MOVING_PISTON || type == Material.STICKY_PISTON || type == Material.REDSTONE_TORCH || type == Material.REDSTONE_BLOCK || type == Material.BEACON || type == Material.END_PORTAL_FRAME || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL || type == Material.RAIL || type == Material.ACTIVATOR_RAIL || type == Material.LADDER || type == Material.SPONGE || type == Material.IRON_BARS || type == Material.OAK_SIGN || type == Material.OAK_WALL_SIGN || type == Material.QUARTZ_BLOCK || type == Material.NETHER_BRICK || type == Material.CHEST) {
                event.setCancelled(true);
            } else if (!this.plugin.monstersReleased && this.plugin.dwarves.contains(event.getPlayer().getPlayer()) && (type == Material.GLOWSTONE || type == Material.TORCH|| type == Material.REDSTONE_LAMP)) {
                this.lightBreakTracker.addLightBreak(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent event) {
        if (event.getBlock().getType() == Material.FIRE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            event.getNewState().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Material t = event.getEntity().getItemStack().getType();
        if (t == Material.GRAVEL || t == Material.SAND) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingRemove(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player && !(event.getRemover()).isOp())
            event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (!(event instanceof HangingBreakByEntityEvent))
            event.setCancelled(true);
    }

    @EventHandler
    public void onHangingRightClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer().isOp())
            return;
        if (event.getRightClicked() instanceof Hanging)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player target = (Player)event.getTarget();
            if (this.plugin.monsters.contains(target.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (this.plugin.monsters.contains(event.getEntity().getName())) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }
}
