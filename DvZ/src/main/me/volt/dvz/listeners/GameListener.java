package main.me.volt.dvz.listeners;

import main.me.volt.dvz.BarCountdown;
import main.me.volt.dvz.DvZ;
import main.me.volt.dvz.gui.ClassGUI;
import main.me.volt.dvz.gui.HatGUI;
import main.me.volt.dvz.gui.KitGUI;
import main.me.volt.dvz.items.DwarfItems;
import main.me.volt.dvz.items.DwarfLoadoutItems;
import main.me.volt.dvz.trackers.LightBreakTracker;
import me.volt.main.mcevolved.MCEvolved;

import com.nisovin.magicspells.events.SpellTargetEvent;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class GameListener implements Listener {
    DvZ plugin;
    LightBreakTracker lightBreakTracker;

    BarCountdown barCountdown;

    public GameListener(DvZ plugin) {
        this.plugin = plugin;
        this.lightBreakTracker = new LightBreakTracker();
        this.barCountdown = new BarCountdown();
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
                    GameListener.this.plugin.dwarves.remove(p);
                    GameListener.this.plugin.dwarfTeam.removeEntry(p.getName());
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
                Bukkit.getScheduler().runTaskLater(plugin, () -> players.sendTitle(ChatColor.DARK_AQUA + p.getName() + ChatColor.RED + " has died!", "", 10, 20, 10), 20L);
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
    public void onDeathSpectatorByPlayer(EntityDamageByEntityEvent event) {
        final Player player = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (event.getDamage() >= player.getHealth()) {
            event.setCancelled(true);
            player.setHealth(20.0D);
            damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2, false));
            damager.playSound(damager.getLocation(), "proc", 1.0F, 1.0F);

            if (player.getKiller() == damager) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (this.plugin.dwarves.contains(player)) {
                        players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_AQUA + player.getName() + ChatColor.WHITE + " was slain by " + ChatColor.DARK_RED + damager.getName() + ChatColor.WHITE + " using " + damager.getInventory().getItemInMainHand().getItemMeta().getDisplayName()));
                    }
                    else if (this.plugin.monsters.contains(player)) {
                        players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_RED + player.getName() + ChatColor.WHITE + " was slain by " + ChatColor.DARK_AQUA + damager.getName() + ChatColor.WHITE + " using " + damager.getInventory().getItemInMainHand().getItemMeta().getDisplayName()));
                    }
                }
            }

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false));

            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.RED + "YOU HAVE DIED. CLICK TO RESPAWN!");

            this.plugin.becomeMonsterSpell.cast(player,1.0F, null);

            boolean isDwarf = this.plugin.dwarves.contains(player.getPlayer());
            boolean isMonster = this.plugin.monsters.contains(player.getPlayer());

            if (isDwarf) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    public void run() {
                        GameListener.this.plugin.dwarves.remove(player);
                        GameListener.this.plugin.dwarfTeam.removeEntry(player.getName());
                        GameListener.this.plugin.monsters.add(player);
                        GameListener.this.plugin.monsterTeam.addEntry(player.getName());
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
                    Bukkit.getScheduler().runTaskLater(plugin, () -> players.sendTitle(ChatColor.DARK_AQUA + player.getName() + ChatColor.RED + " has died!", "", 10, 20, 10), 20L);
                }
            }
            else if (isMonster) {
                this.plugin.monsterKills = this.plugin.monsterKillsScore.getScore() + 1;
                this.plugin.monsterKillsScore.setScore(this.plugin.monsterKills);

                this.plugin.updateScoreboards();
            }

            if (this.plugin.gameRunning) {
                this.plugin.shrineManager.setShrineImmunity(player, false);
                this.plugin.updateScoreboards();
            }
        }
    }

    @EventHandler
    public void onDeathSpectatorChange(EntityDamageEvent event) {
        final Player player = (Player)event.getEntity();
        final Location loc = event.getEntity().getLocation();

        if (event.getDamage() >= player.getHealth() && !this.plugin.monsters.contains(player)) {
            event.setCancelled(true);

            for (Player players : Bukkit.getOnlinePlayers()) {
                if (this.plugin.dwarves.contains(player)) {
                    players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_AQUA + player.getName() + ChatColor.WHITE + " was killed."));
                }
                else if (this.plugin.monsters.contains(player)) {
                    players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_RED + player.getName() + ChatColor.WHITE + " was killed."));
                }
                else {
                    players.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(player.getName() + " was killed."));
                }
            }

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false));

            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.RED + "YOU HAVE DIED. CLICK TO RESPAWN!");

            this.plugin.becomeMonsterSpell.cast(player,1.0F, null);

            boolean isDwarf = this.plugin.dwarves.contains(player.getPlayer());
            boolean isMonster = this.plugin.monsters.contains(player.getPlayer());

            if (isDwarf) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                    public void run() {
                        GameListener.this.plugin.dwarves.remove(player);
                        GameListener.this.plugin.dwarfTeam.removeEntry(player.getName());
                        GameListener.this.plugin.monsters.add(player);
                        GameListener.this.plugin.monsterTeam.addEntry(player.getName());
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
                    Bukkit.getScheduler().runTaskLater(plugin, () -> players.sendTitle(ChatColor.DARK_AQUA +  player.getName() + ChatColor.RED + " has died!", "", 10, 20, 10), 20L);
                }
            }
            else if (isMonster) {
                this.plugin.monsterKills = this.plugin.monsterKillsScore.getScore() + 1;
                this.plugin.monsterKillsScore.setScore(this.plugin.monsterKills);

                this.plugin.updateScoreboards();
            }

            if (this.plugin.gameRunning) {
                this.plugin.shrineManager.setShrineImmunity(player, false);
                this.plugin.updateScoreboards();
            }
        }
    }

    @EventHandler
    public void onSpectatorLeftClick(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
            event.setCancelled(true);
            this.plugin.becomeMonsterSpell.cast(event.getPlayer(),1.0F, null);
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
    public void onServerStatusChange(ServerListPingEvent event) {
        if (!this.plugin.gameRunning) {
            event.setMotd(ChatColor.YELLOW + "" + ChatColor.BOLD + "Map: " + ChatColor.WHITE + "" + ChatColor.ITALIC + this.plugin.mapName + ChatColor.YELLOW + "" + ChatColor.BOLD + " In Lobby: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size() + " players");
        }
        else if (this.plugin.gameRunning && !this.plugin.monstersReleasedFully) {
            event.setMotd(ChatColor.YELLOW + "" + ChatColor.BOLD + "Map: " + ChatColor.WHITE + "" + ChatColor.ITALIC + this.plugin.mapName + ChatColor.GREEN + "" + ChatColor.BOLD + " Online: " + ChatColor.DARK_AQUA + this.plugin.dwarves.size() + " dwarves" + ChatColor.YELLOW + "" + ChatColor.BOLD + " Build Phase");
        }
        else if (this.plugin.gameRunning && this.plugin.monstersReleasedFully) {
            event.setMotd(ChatColor.YELLOW + "" + ChatColor.BOLD + "Map: " + ChatColor.WHITE + "" + ChatColor.ITALIC + this.plugin.mapName + ChatColor.GREEN + "" + ChatColor.BOLD + " Online: " + ChatColor.DARK_AQUA + this.plugin.dwarves.size() + " dwarves" + ChatColor.WHITE + ", " + ChatColor.RED + "" + this.plugin.monsters.size() + " monsters" + ChatColor.YELLOW + " Shrine: " + ChatColor.WHITE + "" + ChatColor.ITALIC + this.plugin.shrineManager.getCurrentShrineName() + " (" + (DvZ.plugin.shrineManager.currentShrine+1) + "/" + DvZ.plugin.shrineManager.shrines.size() + ")");
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

        if (action == Action.LEFT_CLICK_AIR && event.getItem().getType().equals(Material.CHEST) || action == Action.LEFT_CLICK_BLOCK && event.getItem().getType().equals(Material.CHEST)) {
            player.openInventory(this.plugin.srChestInventory);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Jimmy Juice")) {
            event.setCancelled(true);
        }
        if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Mighty Healing Ale")) {
            event.setCancelled(true);
        }
        if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Holy Water")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && !plugin.monsters.contains(player) && plugin.rangerKit.contains(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0));
        }
        else {
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
        }
    }

    @EventHandler
    public void onHatItemLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (event.getItem() == null) {
            return;
        }

        if (action == Action.LEFT_CLICK_AIR && event.getItem().getType().equals(Material.DIAMOND_HELMET)) {
            HatGUI.openGUI(player);
        }
        else if (action == Action.RIGHT_CLICK_AIR && event.getItem().getType().equals(Material.DIAMOND_HELMET) || action == Action.RIGHT_CLICK_BLOCK && event.getItem().getType().equals(Material.DIAMOND_HELMET)) {
            player.getInventory().setHelmet(null);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClassItemLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (event.getItem() == null) {
            return;
        }

        if (action == Action.LEFT_CLICK_AIR && event.getItem().getType().equals(Material.BOOK) || action == Action.LEFT_CLICK_BLOCK && event.getItem().getType().equals(Material.BOOK)) {
            ClassGUI.openGUI(player);
        }
    }

    @EventHandler
    public void onHatMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("Hat Selector") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            ItemStack clicked = event.getCurrentItem();
            Material clickedType = clicked.getType();

            if (clickedType == Material.PURPLE_WOOL) {
                player.getEquipment().setHelmet(DwarfItems.wolfHunterHat);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Wolf Hunter" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.BROWN_WOOL) {
                player.getEquipment().setHelmet(DwarfItems.pharaohHat);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Pharaoh" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.GRAY_WOOL) {
                player.getEquipment().setHelmet(DwarfItems.goggles);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Goggles" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.RED_WOOL) {
                player.getEquipment().setHelmet(DwarfItems.dragonsBreathHat);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Dragons Breath" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.LIGHT_GRAY_WOOL) {
                player.getEquipment().setHelmet(DwarfItems.warriorCap);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Wolf Hunter" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.DIAMOND_HELMET) {
                player.getEquipment().setHelmet(DwarfItems.dwarvenCap);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Dwarven Cap" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.YELLOW_STAINED_GLASS) {
                player.getEquipment().setHelmet(DwarfItems.dwarvenBeard);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Dwarven Beard" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.WHITE_WOOL) {
                player.getEquipment().setHelmet(DwarfItems.santaHat);
                player.sendMessage(ChatColor.GOLD + "Your hat has been set to " + ChatColor.LIGHT_PURPLE + "Dwarven Santa Hat" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.BARRIER) {
                player.getEquipment().setHelmet(null);
                player.sendMessage(ChatColor.GOLD + "You are no longer wearing any hats.");
            }
            event.setCancelled(true);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onDwarfMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        if (!event.getView().getTitle().equals("Select a Kit - BETA")) {
            if (this.plugin.dwarves.contains(player)) {
                if (clicked.getType() != null && clicked.getType() == Material.ARROW) {
                    event.setCancelled(true);
                }
                else if (clicked.getType() != null && clicked.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                    event.setCancelled(true);
                }
                else {
                    event.setCancelled(false);
                }
            }
            else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onClassMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("Select a Kit") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            ItemStack clicked = event.getCurrentItem();
            Material clickedType = clicked.getType();

            if (clickedType == Material.BROWN_DYE) {
                GameListener.this.plugin.warriorKit.add(player);
                GameListener.this.plugin.rangerKit.remove(player);
                GameListener.this.plugin.paladinKit.remove(player);
                player.sendMessage(ChatColor.GOLD + "Your kit has been set to " + ChatColor.LIGHT_PURPLE + "Warrior" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.STONE_SWORD) {
                GameListener.this.plugin.paladinKit.add(player);
                GameListener.this.plugin.rangerKit.remove(player);
                GameListener.this.plugin.warriorKit.remove(player);
                player.sendMessage(ChatColor.GOLD + "Your kit has been set to " + ChatColor.LIGHT_PURPLE + "Paladin" + ChatColor.GOLD + ".");
            }
            else if (clickedType == Material.BOW) {
                GameListener.this.plugin.rangerKit.add(player);
                GameListener.this.plugin.warriorKit.remove(player);
                GameListener.this.plugin.paladinKit.remove(player);
                player.sendMessage(ChatColor.GOLD + "Your kit has been set to " + ChatColor.LIGHT_PURPLE + "Ranger" + ChatColor.GOLD + ".");
            }
            event.setCancelled(true);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onDwarfLoadoutClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (event.getView().getTitle().equals("Select a Kit - BETA") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            if (clicked == null || !clicked.hasItemMeta())
                return;

            if (clicked.getType() == Material.BROWN_DYE) {
                if (clicked.getItemMeta().getLore().contains("§d(Click to Un-equip Class)") || this.plugin.rangerKit.contains(player) || this.plugin.paladinKit.contains(player)) {
                    GameListener.this.plugin.warriorKit.remove(player);

                    event.setCurrentItem(DwarfItems.warriorKit);
                    event.getInventory().setItem(45, DwarfLoadoutItems.pointsRemaining);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    GameListener.this.plugin.warriorKit.add(player);
                    GameListener.this.plugin.rangerKit.remove(player);
                    GameListener.this.plugin.paladinKit.remove(player);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    warriorUnequipped(item);

                    ItemStack points = event.getInventory().getItem(45);
                    ItemMeta pointsMeta = points.getItemMeta();
                    points.setAmount(1);
                    points.setItemMeta(pointsMeta);

                    event.getInventory().setItem(45, points);

                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.STONE_SWORD) {
                if (clicked.getItemMeta().getLore().contains("§d(Click to Un-equip Class)") || this.plugin.rangerKit.contains(player) || this.plugin.warriorKit.contains(player)) {
                    GameListener.this.plugin.paladinKit.remove(player);

                    event.setCurrentItem(DwarfItems.paladinKit);
                    event.getInventory().setItem(45, DwarfLoadoutItems.pointsRemaining);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    GameListener.this.plugin.warriorKit.remove(player);
                    GameListener.this.plugin.rangerKit.remove(player);
                    GameListener.this.plugin.paladinKit.add(player);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    paladinUnequipped(item);

                    ItemStack points = event.getInventory().getItem(45);
                    ItemMeta pointsMeta = points.getItemMeta();
                    points.setAmount(1);
                    points.setItemMeta(pointsMeta);

                    event.getInventory().setItem(45, points);

                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.BOW) {
                if (clicked.getItemMeta().getLore().contains("§d(Click to Un-equip Class)") || this.plugin.paladinKit.contains(player) || this.plugin.warriorKit.contains(player)) {
                    GameListener.this.plugin.rangerKit.remove(player);

                    event.setCurrentItem(DwarfItems.rangerKit);
                    event.getInventory().setItem(45, DwarfLoadoutItems.pointsRemaining);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.3F);
                }
                else {
                    GameListener.this.plugin.rangerKit.add(player);
                    GameListener.this.plugin.warriorKit.remove(player);
                    GameListener.this.plugin.paladinKit.remove(player);

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.0F);

                    ItemStack item = clicked;
                    rangerUnequipped(item);

                    ItemStack points = event.getInventory().getItem(45);
                    ItemMeta pointsMeta = points.getItemMeta();
                    points.setAmount(1);
                    points.setItemMeta(pointsMeta);

                    event.getInventory().setItem(45, points);

                    event.setCurrentItem(item);
                }
            }
            else if (clicked.getType() == Material.MELON_SEEDS) {
                player.openInventory(KitGUI.kitGUI);
            }
            event.setCancelled(true);
        }

        if (event.getView().getTitle().equals("Dwarf Loadout") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            if (clicked.getType() == Material.PUMPKIN_SEEDS) {
                player.openInventory(KitGUI.kitSelectorGUI);
            }
            else if (clicked.getType() == Material.MELON_SEEDS) {
                player.openInventory(KitGUI.kitMeleeGUI);
            }
            event.setCancelled(true);
        }
    }

    public static void warriorUnequipped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Un-equip Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oWarriors are the fighters of the front line.");
        lore.add("§5§oThey wield a powerful sword, this sword gives PROC for 3 seconds.");
        meta.setLore(lore);

        item.setAmount(1);
        item.setItemMeta(meta);
    }

    public static void paladinUnequipped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Un-equip Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oPaladins are the protecting the light.");
        lore.add("§5§oThey wield a powerful mace to hold the line and tools");
        lore.add("§5§oto call forth the light to battle.");
        meta.setLore(lore);

        item.setAmount(1);
        item.setItemMeta(meta);
    }

    public static void rangerUnequipped(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("§a64 points");
        lore.add("§d(Click to Un-equip Class)");
        lore.add(" ");

        lore.add("§eType: §bClass");
        lore.add(" ");

        lore.add("§5§oRangers are the defenders of the keep.");
        lore.add("§5§oThey wield a powerful bow, heal allies, and repair");
        lore.add("§5§othe walls in desperate situations against the demons.");
        meta.setLore(lore);

        item.setAmount(1);
        item.setItemMeta(meta);
    }

    @EventHandler
    public void onMonsterMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("monster_menu") && event.getSlotType() != InventoryType.SlotType.OUTSIDE) {
            ItemStack clicked = event.getCurrentItem();
            Material clickedType = clicked.getType();

            if (clickedType == Material.WOODEN_SWORD) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.BOW) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.ARROW) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.CYAN_DYE) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.BONE) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.SLIME_BALL) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.PRISMARINE_CRYSTALS) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.GOLDEN_PICKAXE) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
            else if (clickedType == Material.DIAMOND_SHOVEL) {
                player.teleport(this.plugin.shrineManager.getMobSpawn());
            }
        }
    }

    @EventHandler
    public void onArmorLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (event.getItem() == null) {
            return;
        }

        if (this.plugin.totalVaultAmount >= 3 && action == Action.LEFT_CLICK_AIR && event.getItem().getType().equals(Material.DIAMOND_CHESTPLATE) && !event.getItem().getType().equals(Material.DIAMOND_CHESTPLATE.getMaxDurability()) || this.plugin.totalVaultAmount >= 3 && action == Action.LEFT_CLICK_BLOCK && event.getItem().getType().equals(Material.DIAMOND_HELMET) && !event.getItem().getType().equals(Material.DIAMOND_HELMET.getMaxDurability())) {
            this.plugin.totalVaultAmount -= 3;
        }
        else if (this.plugin.totalVaultAmount >= 3 && action == Action.LEFT_CLICK_AIR && event.getItem().getType().equals(Material.DIAMOND_LEGGINGS) && !event.getItem().getType().equals(Material.DIAMOND_LEGGINGS.getMaxDurability()) || this.plugin.totalVaultAmount >= 3 && action == Action.LEFT_CLICK_BLOCK && event.getItem().getType().equals(Material.DIAMOND_HELMET) && !event.getItem().getType().equals(Material.DIAMOND_HELMET.getMaxDurability())) {
            this.plugin.totalVaultAmount -= 3;
        }
        else if (this.plugin.totalVaultAmount >= 3 && action == Action.LEFT_CLICK_AIR && event.getItem().getType().equals(Material.DIAMOND_BOOTS) && !event.getItem().getType().equals(Material.DIAMOND_BOOTS.getMaxDurability()) || this.plugin.totalVaultAmount >= 3 && action == Action.LEFT_CLICK_BLOCK && event.getItem().getType().equals(Material.DIAMOND_HELMET) && !event.getItem().getType().equals(Material.DIAMOND_HELMET.getMaxDurability())) {
            this.plugin.totalVaultAmount -= 3;
        }
        else if (this.plugin.totalVaultAmount <= 2) {
            player.sendMessage(ChatColor.AQUA + "You do not have enough gold to repair your armor!");
        }
    }

    public int neededPlayers;
    @EventHandler
    public void onMapChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().equals(this.plugin.newWorld)) {
            if (!plugin.gameRunning) {
                barCountdown.addBarToPlayer(player);
                barCountdown.countdownBar.setProgress(0D);

                for (PotionEffect effect : player.getActivePotionEffects())
                    player.removePotionEffect(effect.getType());

                player.getInventory().setArmorContents(null);
                player.getInventory().clear();

                if (!player.getInventory().contains(DwarfItems.hatSelector)) {
                    player.getInventory().addItem(new ItemStack(DwarfItems.hatSelector));
                }

                if (!player.getInventory().contains(DwarfItems.classSelectorBook)) {
                    player.getInventory().addItem(new ItemStack(DwarfItems.classSelectorBook));
                }
            }

            if (this.plugin.autoStartTime > 0 && !this.plugin.gameRunning && neededPlayers == this.plugin.minPlayers && !this.plugin.override) {
                MCEvolved.startCountdown(this.plugin.autoStartTime, this.plugin.minPlayers);
                barCountdown.countdownBar();
            }
        }
        else if (!player.getWorld().equals(this.plugin.newWorld)) {
            neededPlayers--;

            if (neededPlayers != this.plugin.minPlayers && !this.plugin.gameRunning) {
                MCEvolved.stopCountdown();
                barCountdown.barWaitingForPlayers();
                barCountdown.stopBarTimer();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player players = event.getPlayer();

        neededPlayers++;
        if (!plugin.gameRunning && players.getWorld().equals(this.plugin.newWorld)) {
            barCountdown.addBarToPlayer(event.getPlayer());
            barCountdown.countdownBar.setProgress(0D);

            if (!event.getPlayer().getInventory().contains(DwarfItems.hatSelector)) {
                event.getPlayer().getInventory().addItem(new ItemStack(DwarfItems.hatSelector));
            }

            if (!event.getPlayer().getInventory().contains(DwarfItems.classSelectorBook)) {
                event.getPlayer().getInventory().addItem(new ItemStack(DwarfItems.classSelectorBook));
            }
        }

        if (!event.getPlayer().hasPlayedBefore()) {
            plugin.tips.add(event.getPlayer());
        }
        else {
            plugin.tips.remove(event.getPlayer());
        }

        if (this.plugin.autoStartTime > 0 && !this.plugin.gameRunning && neededPlayers == this.plugin.minPlayers && !this.plugin.override && players.getWorld().equals(this.plugin.newWorld)) {
            MCEvolved.startCountdown(this.plugin.autoStartTime, this.plugin.minPlayers);
            barCountdown.countdownBar();
        }

        if (this.plugin.gameRunning && players.getWorld().equals(this.plugin.newWorld)) {
            this.plugin.initializeScoreboard();
            this.plugin.updateScoreboards();

            final Player player = event.getPlayer();

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

        if (neededPlayers != this.plugin.minPlayers && !this.plugin.gameRunning && event.getPlayer().getWorld().equals(this.plugin.newWorld)) {
            MCEvolved.stopCountdown();
            barCountdown.barWaitingForPlayers();
            barCountdown.stopBarTimer();
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().startsWith("!") && event.getMessage().length() > 1) {
            event.setMessage(event.getMessage().substring(1));
            event.setFormat("[!] " + event.getFormat());
            return;
        }

        if (this.plugin.dwarves.contains(event.getPlayer())) {
            Iterator<Player> iter = event.getRecipients().iterator();
            while (iter.hasNext()) {
                if (this.plugin.monsters.contains((iter.next()).getPlayer())) {
                    iter.remove();
                }
            }
        }
        else if (this.plugin.monsters.contains(event.getPlayer())) {
            Iterator<Player> iter = event.getRecipients().iterator();
            while (iter.hasNext()) {
                if (this.plugin.dwarves.contains((iter.next()).getPlayer())) {
                    iter.remove();
                }
            }
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
                    GameListener.this.plugin.dwarfTeam.removeEntry(player.getPlayer().getName());
                    GameListener.this.plugin.monsters.add(player.getPlayer());
                    GameListener.this.plugin.monsterTeam.removeEntry(player.getPlayer().getName());

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

    int tipCount = 0;
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

        if (event.getBlock().getType() == Material.COBBLESTONE) {
            if (this.plugin.tips.contains(event.getPlayer()) && tipCount <= 0) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "====================[DvZ Tutorial]===================");
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Cobblestone is very weak against monsters. You can use");
                event.getPlayer().sendMessage(ChatColor.YELLOW + "mortar to reinforce cobblestone into stone bricks.");
                event.getPlayer().sendMessage(ChatColor.GOLD + "===================================================");

                tipCount++;
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

        if (this.plugin.monsters.contains(player) && event.getCause() == EntityDamageEvent.DamageCause.FALL || this.plugin.monsters.contains(player) && event.getCause() == EntityDamageEvent.DamageCause.FIRE || this.plugin.monsters.contains(player) && event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || this.plugin.monsters.contains(player) && event.getCause() == EntityDamageEvent.DamageCause.DROWNING || this.plugin.monsters.contains(player) && event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || this.plugin.monsters.contains(player) && event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
        }
        else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onDamage2(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE && event.getDamager() instanceof Player && this.plugin.monsters.contains((((Player) event.getDamager()).getPlayer()))) {
            event.setCancelled(true);
        }
    }

    private boolean sameTeam(Player victim, Player attacker) {
        if (this.plugin.dwarves.contains(victim) && this.plugin.dwarves.contains(attacker)) {
            return true;
        }
        else if (!this.plugin.dwarves.contains(victim) && !this.plugin.dwarves.contains(attacker)) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getEntity();

        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        if (this.plugin.dwarves.contains(player)) {
            if (event.getDamager() instanceof Player && player instanceof Player) {
                if (chestplate.getItemMeta() instanceof Damageable && chestplate.getItemMeta() != null) {
                    if (((Damageable) chestplate.getItemMeta()).getDamage() > 510) {
                        player.setFoodLevel(11);
                    }
                    else if (((Damageable) chestplate.getItemMeta()).getDamage() > 470) {
                        player.setFoodLevel(12);
                    }
                    else if (((Damageable) chestplate.getItemMeta()).getDamage() > 390) {
                        player.setFoodLevel(13);
                    }
                    else if (((Damageable) chestplate.getItemMeta()).getDamage() > 315) {
                        player.setFoodLevel(14);
                    }
                    else if (((Damageable) chestplate.getItemMeta()).getDamage() > 245) {
                        player.setFoodLevel(15);
                    }
                    else if (((Damageable) chestplate.getItemMeta()).getDamage() > 175) {
                        player.setFoodLevel(16);
                    }
                    else if (((Damageable) chestplate.getItemMeta()).getDamage() > 90) {
                        player.setFoodLevel(17);
                    }
                    else if (((Damageable) chestplate.getItemMeta()).getDamage() > 10) {
                        player.setFoodLevel(18);
                    }
                }
                else {
                    return;
                }

                if (leggings.getItemMeta() instanceof Damageable && leggings.getItemMeta() != null) {
                    if (((Damageable) leggings.getItemMeta()).getDamage() > 10) {
                        player.setFoodLevel(19);
                    }
                    else if (((Damageable) leggings.getItemMeta()).getDamage() > 90) {
                        player.setFoodLevel(18);
                    }
                    else if (((Damageable) leggings.getItemMeta()).getDamage() > 175) {
                        player.setFoodLevel(17);
                    }
                    else if (((Damageable) leggings.getItemMeta()).getDamage() > 245) {
                        player.setFoodLevel(16);
                    }
                    else if (((Damageable) leggings.getItemMeta()).getDamage() > 315) {
                        player.setFoodLevel(15);
                    }
                    else if (((Damageable) leggings.getItemMeta()).getDamage() > 390) {
                        player.setFoodLevel(14);
                    }
                    else if (((Damageable) leggings.getItemMeta()).getDamage() > 470) {
                        player.setFoodLevel(13);
                    }
                }
                else {
                    return;
                }

                if (boots.getItemMeta() instanceof Damageable && boots.getItemMeta() != null) {
                    if (((Damageable) leggings.getItemMeta()).getDamage() > 10) {
                        player.setFoodLevel(19);
                    }
                    else if (((Damageable) boots.getItemMeta()).getDamage() > 175) {
                        player.setFoodLevel(18);
                    }
                    else if (((Damageable) boots.getItemMeta()).getDamage() > 315) {
                        player.setFoodLevel(17);
                    }
                    else if (((Damageable) boots.getItemMeta()).getDamage() > 350) {
                        player.setFoodLevel(16);
                    }
                    else if (((Damageable) boots.getItemMeta()).getDamage() > 415) {
                        player.setFoodLevel(15);
                    }
                }
                else {
                    return;
                }
            }
        }
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
                if (!this.plugin.dwarves.contains(target)) {
                    event.setCancelled(true);
                }
            }
            else if (!this.plugin.monsters.contains(target)) {
                event.setCancelled(true);
            }
        }
        else if (this.plugin.monsters.contains(player)) {
            if (event.getSpell().isBeneficial()) {
                event.setCancelled(true);
            }
            else if (!this.plugin.dwarves.contains(target)) {
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
        event.setCancelled(true);
        event.setFoodLevel(20);
        (event.getEntity()).setSaturation(20.0F);
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
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
        else if (event.getSlotType() == InventoryType.SlotType.ARMOR && this.plugin.monsters.contains(event.getWhoClicked())) {
            event.setCancelled(true);
        }
        else if (event.getSlot() == 39)  {
            event.setCancelled(true);
        }
    }

    int count = 0;
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setExpToDrop(0);
        if (!event.getPlayer().hasPermission("game.ignore") || event.getPlayer().isOp()) {
            Material type = event.getBlock().getType();

            if (type == Material.IRON_BLOCK && !event.getPlayer().isOp() || type == Material.DIAMOND_BLOCK && !event.getPlayer().isOp() || type == Material.GLASS && !event.getPlayer().isOp() || type == Material.PISTON_HEAD && !event.getPlayer().isOp() || type == Material.PISTON && !event.getPlayer().isOp() || type == Material.MOVING_PISTON && !event.getPlayer().isOp() || type == Material.STICKY_PISTON && !event.getPlayer().isOp() || type == Material.REDSTONE_TORCH && !event.getPlayer().isOp() || type == Material.REDSTONE_BLOCK && !event.getPlayer().isOp() || type == Material.BEACON && !event.getPlayer().isOp() || type == Material.END_PORTAL_FRAME && !event.getPlayer().isOp() || type == Material.POWERED_RAIL && !event.getPlayer().isOp() || type == Material.DETECTOR_RAIL && !event.getPlayer().isOp() || type == Material.RAIL && !event.getPlayer().isOp() || type == Material.ACTIVATOR_RAIL && !event.getPlayer().isOp() || type == Material.LADDER && !event.getPlayer().isOp() || type == Material.SPONGE && !event.getPlayer().isOp() || type == Material.IRON_BARS && !event.getPlayer().isOp() || type == Material.OAK_SIGN && !event.getPlayer().isOp() || type == Material.OAK_WALL_SIGN && !event.getPlayer().isOp() || type == Material.QUARTZ_BLOCK && !event.getPlayer().isOp() || type == Material.NETHER_BRICK && !event.getPlayer().isOp() || type == Material.CHEST && !event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
            else if (!this.plugin.monstersReleased && this.plugin.dwarves.contains(event.getPlayer().getPlayer()) && (type == Material.GLOWSTONE || type == Material.TORCH|| type == Material.REDSTONE_LAMP)) {
                this.lightBreakTracker.addLightBreak(event.getPlayer());
            }

            if (type == Material.GOLD_ORE && this.plugin.dwarves.contains(event.getPlayer())) {
                this.plugin.totalVaultAmount++;

                if (this.plugin.tips.contains(event.getPlayer()) && count <= 0) {
                    event.getPlayer().sendMessage(ChatColor.GOLD + "====================[DvZ Tutorial]===================");
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "Breaking gold ore puts gold into the vault. Gold is essential");
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "for repairing armor and surviving.");
                    event.getPlayer().sendMessage(ChatColor.GOLD + "===================================================");

                    count++;
                }
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
                if (player.canSee(p))
                    if (this.plugin.dwarves.contains(p)) {
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
                            m.put(p.getName().toLowerCase(), ChatColor.DARK_RED + p.getName());
                        }
                    }
                else {
                        o.put(p.getName().toLowerCase(), p.getDisplayName());
                }
            }
            int c = 0;
            int lineLength = 55;

            player.sendMessage(ChatColor.YELLOW + "PLAYERS ONLINE (" + (d.size() + m.size() + o.size()) + "):");

            if (d.size() > 0) {
                player.sendMessage(ChatColor.AQUA + "  Dwarves (" + d.size() + "):");
                c = 0;
                String msg = "    ";
                for (String name : d.values()) {
                    if (ChatColor.stripColor(msg).length() + ChatColor.stripColor(name).length() + 2 > lineLength) {
                        player.sendMessage(msg);
                        msg = "    ";
                    }
                    msg = msg + name;
                    if (++c < d.size())
                        msg = msg + ChatColor.WHITE + ", ";
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
        if (this.plugin.monsters.contains(event.getEntity())) {
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }
}
