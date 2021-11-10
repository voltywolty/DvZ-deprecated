package main.me.volt.dvz.listeners;

import main.me.volt.dvz.BarCountdown;
import main.me.volt.dvz.DvZ;
import main.me.volt.dvz.screens.LobbyScreen;
import main.me.volt.dvz.trackers.LightBreakTracker;
import me.volt.main.mcevolved.MCEvolved;

import com.nisovin.magicspells.events.SpellTargetEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

import java.util.TreeMap;

public class GameListener implements Listener {
    DvZ plugin;
    LightBreakTracker lightBreakTracker;

    BarCountdown barCountdown;
    LobbyScreen lobbyScreen;

    public GameListener(DvZ plugin) {
        this.plugin = plugin;
        this.lightBreakTracker = new LightBreakTracker();
        this.barCountdown = new BarCountdown();
        this.lobbyScreen = new LobbyScreen();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        final Player p = event.getEntity();
        event.getDrops().clear();
        event.setDroppedExp(0);

        boolean isDwarf = this.plugin.dwarves.contains(p.getPlayer());
        boolean isMonster = this.plugin.monsters.contains(p.getPlayer());

        p.setDisplayName(p.getName());

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

    int counter = 0;
    @EventHandler
    public void onCompassRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_AIR && event.getItem().getType().equals(Material.COMPASS)) {
            if (counter == 0) {
                player.setCompassTarget(plugin.quarryLocation);
                player.sendMessage(ChatColor.YELLOW + "Your compass is now pointing at " + ChatColor.AQUA + "the gold mine and quarry" + ChatColor.YELLOW + ".");
                counter++;
            }
            else if (counter == 1) {
                player.setCompassTarget(plugin.blacksmithTablesLocation);
                player.sendMessage(ChatColor.YELLOW + "Your compass is now pointing at " + ChatColor.AQUA + "the blacksmith" + ChatColor.YELLOW + ".");
                counter++;
            }
            else if (counter == 2) {
                player.setCompassTarget(plugin.sawmillLocation);
                player.sendMessage(ChatColor.YELLOW + "Your compass is now pointing at " + ChatColor.AQUA + "the lumber mill and oil" + ChatColor.YELLOW + ".");
                counter++;
            }
            else if (counter == 3) {
                player.setCompassTarget(plugin.mobSpawn);
                player.sendMessage(ChatColor.YELLOW + "Your compass is now pointing at " + ChatColor.AQUA + "the monster spawn" + ChatColor.YELLOW + ".");
                counter = 0;
            }
        }
    }

    @EventHandler
    public void onResourceChestLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (event.getItem() == null) {
            return;
        }

        if (action == Action.LEFT_CLICK_AIR && event.getItem().getType().equals(Material.CHEST)) {
            player.openInventory(this.plugin.srChestInventory);
        }
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() instanceof Chest) {
                event.getPlayer().openInventory(this.plugin.srChestInventory);
            }
            else {
                return;
            }
        }
        else {
            return;
        }
    }

    @EventHandler
    public void onLobbyBookLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (event.getItem() == null) {
            return;
        }

        if (action == Action.LEFT_CLICK_AIR && event.getItem().getType() == Material.BOOK || action == Action.LEFT_CLICK_BLOCK && event.getItem().getType() == Material.BOOK) {
            player.openInventory(this.lobbyScreen.menuInventory);
        }
    }

    @EventHandler
    public void onLobbyInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;

        if (event.getClickedInventory().getHolder() instanceof LobbyScreen) {
            event.setCancelled(true);

            if (event.isLeftClick()) {
                Player player = (Player) event.getWhoClicked();
                if (event.getCurrentItem() == null) {
                    return;
                }

                if (event.getCurrentItem().getType() == Material.NAME_TAG) {
                    player.openInventory(lobbyScreen.titleInventory);
                }

                if (event.getCurrentItem().getType() == Material.DIAMOND_AXE) {
                    player.sendMessage(ChatColor.GOLD + "You will now have the Paladin name when the game starts.");
                }
            }
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
        if (!plugin.gameRunning) {
            barCountdown.addBarToPlayer(event.getPlayer());
            barCountdown.countdownBar.setProgress(0D);

//            if (event.getPlayer().getInventory().contains(DwarfItems.lobbyMenuBook)) {
//                return;
//            }
//            else {
//                event.getPlayer().getInventory().addItem(DwarfItems.lobbyMenuBook);
//            }
        }

        if (this.plugin.autoStartTime > 0 && !this.plugin.gameRunning && neededPlayers == this.plugin.minPlayers && !this.plugin.override) {
            MCEvolved.startCountdown(this.plugin.autoStartTime, this.plugin.minPlayers);
            barCountdown.countdownBar();
        }

        if (this.plugin.gameRunning) {
            this.plugin.initializeScoreboard();
            this.plugin.updateScoreboards();

            final Player player = event.getPlayer();

            barCountdown.removeBarFromPlayer(player);

            if (this.plugin.shrineBarManager != null) {
                this.plugin.shrineBarManager.sendBarToPlayer(event.getPlayer());
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
            barCountdown.barWaitingForPlayers();
        }
        else if (!this.plugin.override) {
            MCEvolved.stopCountdown();
            barCountdown.removeBarFromAll();
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

                    if (GameListener.this.plugin.shrineBarManager != null) {
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

        if (event.getBlock().getType() == Material.CHEST) {
            event.setCancelled(true);
        }

        if (event.getBlock().getType() == Material.ZOMBIE_HEAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();

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

        if (this.plugin.monsters.contains(player.getPlayer()) && event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.DROWNING || event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
        }
        else {
            event.setCancelled(false);
        }
    }

    private boolean sameTeam(Player victim, Player attacker) {
        if (this.plugin.dwarves.contains(victim.getPlayer()) && this.plugin.dwarves.contains(attacker.getPlayer())) {
            return true;
        }
        else if (!this.plugin.dwarves.contains(victim.getPlayer()) && !this.plugin.dwarves.contains(attacker.getPlayer())) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onTeamDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = null;

        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }
            attacker = (Player) projectile.getShooter();
        }

        if (victim == attacker) {
            return;
        }

        if (attacker == null) {
            return;
        }

        if (sameTeam(victim, attacker)) {
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
        if (!event.getPlayer().hasPermission("game.ignore") || event.getPlayer().isOp()) {
            Material type = event.getBlock().getType();
            if (type == Material.IRON_BLOCK || type == Material.DIAMOND_BLOCK || type == Material.GLASS || type == Material.PISTON_HEAD || type == Material.PISTON || type == Material.MOVING_PISTON || type == Material.STICKY_PISTON || type == Material.REDSTONE_TORCH || type == Material.REDSTONE_BLOCK || type == Material.BEACON || type == Material.END_PORTAL_FRAME || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL || type == Material.RAIL || type == Material.ACTIVATOR_RAIL || type == Material.LADDER || type == Material.SPONGE || type == Material.IRON_BARS || type == Material.OAK_SIGN || type == Material.OAK_WALL_SIGN || type == Material.QUARTZ_BLOCK || type == Material.NETHER_BRICK || type == Material.CHEST && !event.getPlayer().isOp()) {
                event.setCancelled(true);
            } else if (!this.plugin.monstersReleased && this.plugin.dwarves.contains(event.getPlayer().getPlayer()) && (type == Material.GLOWSTONE || type == Material.TORCH|| type == Material.REDSTONE_LAMP)) {
                this.lightBreakTracker.addLightBreak(event.getPlayer());
            }

            if (type == Material.GOLD_ORE && this.plugin.dwarves.contains(event.getPlayer())) {
                DvZ.plugin.totalVaultAmount++;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
       String cmd = event.getMessage().split(" ")[0].toLowerCase();
       if (cmd.equals("/list") || cmd.equals("/who")) {
           event.setCancelled(true);
           Player player = event.getPlayer();

           TreeMap<String, String> d = new TreeMap<>();
           TreeMap<String, String> m = new TreeMap<>();
           TreeMap<String, String> o = new TreeMap<>();

           for (Player p : Bukkit.getOnlinePlayers()) {
               if (player.canSee(p)) {
                   if (this.plugin.dwarves.contains(p.getName())) {
                       if (!p.getDisplayName().contains(p.getName())) {
                            d.put(p.getName().toLowerCase(), p.getDisplayName() + "(" + p.getName() + ")");
                       }
                       else {
                           d.put(p.getName().toLowerCase(), p.getDisplayName());
                       }
                   }
                   else if (this.plugin.monsters.contains(p.getName())) {
                       if (!p.getDisplayName().contains(p.getName())) {
                           m.put(p.getName().toLowerCase(), p.getDisplayName() + "(" + p.getName() + ")");
                       }
                       else {
                           m.put(p.getName().toLowerCase(), p.getDisplayName());
                       }
                   }
                   else {
                       o.put(p.getName().toLowerCase(), p.getDisplayName());
                   }
               }
           }
           int c = 0;
           int lineLength = 55;

           player.sendMessage(ChatColor.YELLOW + "PLAYERS ONLINE (" + (d.size() + m.size() + o.size()) + "):");

           if (d.size() > 0) {
               player.sendMessage(ChatColor.AQUA + "Dwarves (" + d.size() + "):");

               c = 0;
               String msg = " ";
               for (String name : d.values()) {
                   if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                       player.sendMessage(msg);
                       msg = "";
                   }
                   msg = msg + name;
                   if (c++ < d.size()) {
                       msg = msg + ChatColor.WHITE + ", ";
                   }
               }
               player.sendMessage(msg);
           }
           if (m.size() > 0) {
               player.sendMessage(ChatColor.RED + "  Monsters (" + m.size() + "):");

               c = 0;
               String msg = "    ";
               for (String name : m.values()) {
                   if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                       player.sendMessage(msg);
                       msg = "    ";
                   }
                   msg = msg + name;
                   if (++c < m.size())
                       msg = msg + ChatColor.WHITE + ", ";
               }
               player.sendMessage(msg);
           }
           if (o.size() > 0) {
               player.sendMessage(ChatColor.GRAY + "  Others:");
               c = 0;
               String msg = "    ";
               for (String name : o.values()) {
                   if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                       player.sendMessage(msg);
                       msg = "    ";
                   }
                   msg = msg + name;
                   if (++c < o.size())
                       msg = msg + ChatColor.WHITE + ", ";
               }
               player.sendMessage(msg);
           }
       }
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
