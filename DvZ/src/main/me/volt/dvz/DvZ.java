package main.me.volt.dvz;

import com.nisovin.magicspells.util.managers.PassiveManager;

import main.me.volt.dvz.conditions.*;
import main.me.volt.dvz.events.PlagueEvent;
import main.me.volt.dvz.events.RageEvent;
import main.me.volt.dvz.events.doom.DireWolvesEvent;
import main.me.volt.dvz.events.doom.DoomEvent;
import main.me.volt.dvz.events.doom.GoblinSquadEvent;
import main.me.volt.dvz.events.doom.GolemEvent;
import main.me.volt.dvz.gui.HatGUI;
import main.me.volt.dvz.items.DwarfItems;
import main.me.volt.dvz.items.DwarfLoadoutItems;
import main.me.volt.dvz.items.cosmetic.HatItems;
import main.me.volt.dvz.listeners.GUIListener;
import me.volt.main.mcevolved.GameMode;
import me.volt.main.mcevolved.MCEvolved;

import main.me.volt.dvz.passives.ShrineDestroyedListener;
import main.me.volt.dvz.utils.Bleeder;
import main.me.volt.dvz.listeners.GameListener;
import main.me.volt.dvz.managers.ShrineManager;
import main.me.volt.dvz.utils.ShrineBarManager;
import main.me.volt.dvz.events.GameEvent;

import com.nisovin.magicspells.util.managers.ConditionManager;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.BlockUtils;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.util.*;

import static main.me.volt.dvz.gui.HatGUI.setHats;
import static main.me.volt.dvz.gui.KitGUI.*;

public class DvZ extends JavaPlugin implements GameMode, Listener {
    public static DvZ plugin;

    public boolean gameRunning = false, gameEnded = false;
    public boolean monstersReleased = false, monstersReleasedFully = false;

    public Set<Player> tips;

    public boolean override = false;

    public long gameStartTime = 0L;

    public String mapName;

    public int autoStartTime, monsterReleaseTime;
    public int minPlayers, minPlayersForHeroes;
    public int scoreInterval, percentMonsters;
    public int monsterSpecialInterval;

    public boolean killDwarvesOnJoin;
    public int startEndTimerAtPercent, endTimerDuration;

    public Spell becomeDwarfSpell, becomeMonsterSpell, monsterSpecialSpell;

    public List<String> startCommands, monsterReleaseCommands, specialDwarves;
    public Map<Player, String> heroes;

    public Location mapSpawn, mobSpawn, dwarfSpawn;
    public Location quarryLocation, blacksmithLocation, sawmillLocation, oilLocation;

    public ShrineManager shrineManager;
    public ShrineBarManager shrineBarManager;
    public double shrinePower = 200.0D;

    public Set<Player> dwarves, monsters;
    public Set<Player> warriorKit, paladinKit, rangerKit;

    ScoreboardManager manager;
    Scoreboard scoreboard;
    Objective objective;

    public Team dwarfTeam, monsterTeam;

    public Score vaultScore, remainingDwarvesScore, monsterKillsScore, timeScore, doomScore;
    public int totalVaultAmount = 500;
    public int monsterKills, totalTime;
    private int doomTimer = 1200;
    private int doomTimerAfter = 20;

    BukkitTask counterTask, monsterReleaseTask, endTask, monsterSpecialTask, updateScoreboardTask;

    private Bleeder bleeder;

    public GameEvent gameEvent;
    public DoomEvent doomEvent;

    public BarCountdown barCountdown;

    public Random random = new Random();

    private ConditionManager conditionManager = new ConditionManager();
    private PassiveManager passiveManager = new PassiveManager();

    public Inventory srChestInventory;

    public int getGameModeId() {
        return 1;
    }

    public String getGameModeCode() {
        return "DvZ";
    }

    public String getGameModeName() {
        return "Dwarves vs. Zombies";
    }

    public void onLoad() {
        plugin = this;
        loadMagicSpellsConditions();
    }

    public void loadMagicSpellsConditions() {
        conditionManager.addCondition("isdwarf", IsDwarfCondition.class);
        conditionManager.addCondition("ismonster", IsMonsterCondition.class);

        conditionManager.addCondition("monstersreleased", MonstersReleasedCondition.class);
        conditionManager.addCondition("nearmapspawn", NearMapSpawnCondition.class);

        conditionManager.addCondition("dwarvesremaininglessthan", DwarvesRemainingLessThanCondition.class);
        conditionManager.addCondition("goldremaininggreaterthan", GoldRemainingGreaterThan.class);

        conditionManager.addCondition("shrinepowerlessthan", ShrinePowerLessThanCondition.class);
        conditionManager.addCondition("shrinedistancelessthan", ShrineDistanceLessThanCondition.class);
        conditionManager.addCondition("shrinedistancemorethan", ShrineDistanceMoreThanCondition.class);

        passiveManager.addListener("shrinedestroyed", ShrineDestroyedListener.class);
    }

    public void onEnable() {
        HatItems.init();
        DwarfItems.init();
        DwarfLoadoutItems.init();
        srChestInventory = this.getServer().createInventory(null, 54, ChatColor.DARK_BLUE + "Shared Resources");

        // WARNING - removing these will cause nothing to
        // appear in the inventories that players open
        setHats();
        kitSelectorItems();
        dwarfLoadoutItems();
        loadoutMeleeItems();

        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            System.out.println("Config file not found! Creating default config file...");
            saveDefaultConfig();
        }
        else {
            System.out.println("Config file found!");
        }

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(configFile);
        }
        catch (Exception e) {
            getLogger().severe("FAILED TO LOAD CONFIG FILE!");
            e.printStackTrace();
            setEnabled(false);
            return;
        }

        setConfig(config);

        this.heroes = new HashMap<>();

        Bukkit.getServer().getWorlds().get(0).setDifficulty(Difficulty.HARD);

        Bukkit.getServer().getWorlds().get(0).setStorm(false);
        Bukkit.getServer().getWorlds().get(0).setWeatherDuration(300000);

        Bukkit.getServer().getWorlds().get(0).setGameRule(GameRule.DO_TILE_DROPS, false);
        Bukkit.getServer().getWorlds().get(0).setGameRule(GameRule.SPAWN_RADIUS, 1);
        Bukkit.getServer().getWorlds().get(0).setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        Bukkit.getServer().getWorlds().get(0).setGameRule(GameRule.KEEP_INVENTORY, false);
        Bukkit.getServer().getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

        this.tips = Collections.synchronizedSet(new HashSet<>());

        this.dwarves = Collections.synchronizedSet(new HashSet<>());
        this.monsters = Collections.synchronizedSet(new HashSet<>());

        this.warriorKit = Collections.synchronizedSet(new HashSet<>());
        this.paladinKit = Collections.synchronizedSet(new HashSet<>());
        this.rangerKit = Collections.synchronizedSet(new HashSet<>());

        if (this.becomeDwarfSpell == null) {
            getLogger().severe("BECOME-DWARF-SPELL IS INVALID!");
            setEnabled(false);
            return;
        }

        if (this.becomeMonsterSpell == null) {
            getLogger().severe("BECOME-MONSTER-SPELL IS INVALID!");
            setEnabled(false);
            return;
        }

        if (this.monsterSpecialSpell == null) {
            getLogger().warning("MONSTER-SPECIAL-SPELL IS INVALID!");
        }

        this.shrineManager = new ShrineManager(this, config);

        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        this.shrineBarManager = new ShrineBarManager(this.shrineManager.getCurrentShrineName());

        int r = this.random.nextInt(15);

        if (r < 1) {
            this.gameEvent = new RageEvent(this);
        }
        else if (r < 15) {
            this.gameEvent = new PlagueEvent(this);
        }

        MCEvolved.initializeGameMode(this);

        barCountdown = new BarCountdown();
        barCountdown.barWaitingForPlayers();
    }

    public void onDisable() {
        endGame();
    }

    private void setConfig(YamlConfiguration config) {
        this.mapName = config.getString("map-name", "map");
        this.autoStartTime = config.getInt("auto-start-time", 0);

        this.minPlayers = config.getInt("min-players", 0);
        this.minPlayersForHeroes = config.getInt("min-players-for-heroes", 5);

        this.monsterReleaseTime = config.getInt("monster-release-time", 0);
        this.scoreInterval = config.getInt("score-interval", 30);
        this.percentMonsters = config.getInt("percent-monsters", 30);

        this.monsterSpecialInterval = config.getInt("monster-special-interval", 480);

        this.becomeDwarfSpell = MagicSpells.getSpellByInternalName(config.getString("become-dwarf-spell", "dwarf_become_dwarf"));
        this.becomeMonsterSpell = MagicSpells.getSpellByInternalName(config.getString("become-monster-spell", "monster_forcegroup"));
        this.monsterSpecialSpell = MagicSpells.getSpellByInternalName(config.getString("monster-special-spell", "monster-special"));

        this.killDwarvesOnJoin = config.getBoolean("kill-dwarves-on-join", false);
        this.startEndTimerAtPercent = config.getInt("start-end-timer-at-percent", 20);
        this.endTimerDuration = config.getInt("end-timer-duration", 300);

        this.startCommands = config.getStringList("start-commands");
        this.monsterReleaseCommands = config.getStringList("monster-release-commands");
        this.specialDwarves = config.getStringList("special-dwarves");

        String mapSpawnString = config.getString("map-spawn", "");
        String mobSpawnString = config.getString("mob-spawn", "");
        String dwarfSpawnString = config.getString("dwarf-spawn", "");

        String quarryLocString = config.getString("quarry-location", "");
        String blacksmithTableLocString = config.getString("blacksmith-location", "");
        String sawmillLocString = config.getString("sawmill-location", "");
        String oilLocString = config.getString("oil-location", "");

        if (quarryLocString.isEmpty()) {
            this.quarryLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = quarryLocString.split(",");
            this.quarryLocation = new Location(Bukkit.getServer().getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        if (blacksmithTableLocString.isEmpty()) {
            this.blacksmithLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = blacksmithTableLocString.split(",");
            this.blacksmithLocation = new Location(Bukkit.getServer().getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        if (sawmillLocString.isEmpty()) {
            this.sawmillLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = sawmillLocString.split(",");
            this.sawmillLocation = new Location(Bukkit.getServer().getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        if (oilLocString.isEmpty()) {
            this.oilLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = oilLocString.split(",");
            this.oilLocation = new Location(Bukkit.getServer().getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        if (mapSpawnString.isEmpty()) {
            this.mapSpawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = mapSpawnString.split(",");
            this.mapSpawn = new Location(Bukkit.getServer().getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        if (mobSpawnString.isEmpty()) {
            this.mobSpawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = mobSpawnString.split(",");
            this.mobSpawn = new Location(Bukkit.getServer().getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        if (dwarfSpawnString.isEmpty()) {
            this.dwarfSpawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = dwarfSpawnString.split(",");
            this.dwarfSpawn = new Location(Bukkit.getServer().getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("startgame")) {
            if (!this.gameRunning && !this.gameEnded) {
                startGame();
                MCEvolved.stopCountdown();
                barCountdown.removeBarFromAll();
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Game started.");
            }
            else {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Game has already started.");
            }
        }
        else if (command.getName().equalsIgnoreCase("override")) {
            if (!this.gameRunning) {
                this.override = true;
                this.barCountdown.stopBarTimer();
                this.barCountdown.removeBarFromAll();
                MCEvolved.stopCountdown();
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Override mode enabled. You will need to manually /startgame and /releasemonsters.");
            }
            else if (!this.monstersReleased) {
                if (this.monsterReleaseTask != null) {
                    this.monsterReleaseTask.cancel();
                }
                this.override = true;
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Override mode enabled. You will need to manually /releasemonsters.");
            }
            else {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "The game has already started and monsters have already been released.");
            }
        }
        else if (command.getName().equalsIgnoreCase("event")) {
            PlagueEvent plagueEvent = new PlagueEvent(this);
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Active event: " + this.gameEvent.getName());
                if (this.override) {
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Except the game is overwritten!");
                }
                return true;
            }
            if (this.monstersReleased) {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "The monsters are already released.");
                return true;
            }

            GameEvent event = null;
            if (args[0].equalsIgnoreCase("plague")) {
                plagueEvent = new PlagueEvent(this);
                this.gameEvent = plagueEvent;
            }
            else if (args[0].equalsIgnoreCase("rage")) {
                RageEvent rageEvent = new RageEvent(this);
                this.gameEvent = rageEvent;
            }
            else {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Usage: /event [plague|rage]");
                return true;
            }

            if (!this.gameRunning || !this.override) {
                this.gameEvent = plagueEvent;
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Game event set to " + this.gameEvent.getName());
            }
            else {
                this.gameEvent.run();
                this.monstersReleased = true;
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Game event set to " + this.gameEvent.getName() + " and activated.");
            }
        }
        else if (command.getName().equalsIgnoreCase("releasemonsters")) {
            if (this.gameRunning) {
                if (!this.monstersReleased) {
                    beginMonsterRelease();
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Monsters released.");
                } else {
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Monsters have already been released.");
                }
            } else {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Game hasn't started.");
            }
        }
        else if (command.getName().equalsIgnoreCase("warpshrine")) {
            if (args.length == 1) {
                Player player = Bukkit.getPlayerExact(args[0]);

                if (player != null) {
                    player.teleport(this.shrineManager.getCurrentShrineForTeleport());
                }
                else if (sender instanceof Player) {
                    ((Player)sender).teleport(this.shrineManager.getCurrentShrineForTeleport());
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("setdwarf")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Usage: /setdwarf playername");
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "No player found");
                }
                else {
                    this.gameEvent.setDwarf(player);
                    this.monsters.remove(player.getPlayer());
                    setAsDwarf(player);
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Player " + player.getName() + " is now a dwarf.");
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("setmonster")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Usage: /setmonster playername");
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "No player found");
                }
                else {
                    this.dwarves.remove(player.getPlayer());
                    this.monsters.add(player.getPlayer());

                    this.dwarfTeam.removeEntry(player.getPlayer().getName());
                    this.monsterTeam.addEntry(player.getPlayer().getName());

                    this.becomeMonsterSpell.cast(player, 1.0F, null);
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Player " + player.getName() + " is now a monster.");
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("sethero")) {
            if (gameRunning) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "No player found");
                }
                else {
                    sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Player " + player.getName() + " is now a hero.");
                }

                if (args[1].equalsIgnoreCase("charles")){
                    setAsCharles(player);
                }
                else if (args[1].equalsIgnoreCase("marsh")) {
                    setAsMarsh(player);
                }
                else if (args[1].equalsIgnoreCase("apollo")) {
                    setAsApollo(player);
                }
            }
            else if (args.length != 2) {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Usage: /sethero playername [charles|marsh|apollo]");
            }
        }
        else if (command.getName().equalsIgnoreCase("specialmonsterinterval")) {
            if (args.length == 1 && args[0].matches("^[0-9]+$")) {
                this.monsterSpecialInterval = Integer.parseInt(args[0]);

                if (this.monstersReleased) {
                    if (this.monsterSpecialTask != null) {
                        this.monsterSpecialTask.cancel();
                    }
                    startMonsterSpecialTask();
                }
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Special monster interval set to " + this.monsterSpecialInterval + " seconds.");
            }
            else {
                sender.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Invalid interval.");
            }
        }
        else if (command.getName().equalsIgnoreCase("compass")) {
            if (gameRunning) {
                if (sender instanceof Player && this.dwarves.contains(((Player) sender).getPlayer())) {
                    Player player = (Player) sender;
                    if (player.getInventory().contains(Material.COMPASS)) {
                        player.sendMessage("You already have a compass!");
                    }
                    else {
                        player.getInventory().addItem(DwarfItems.dwarvenCompass);
                        player.sendMessage(ChatColor.GOLD + "You have been given a " + ChatColor.AQUA + "compass" + ChatColor.GOLD + ".");
                    }
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("sharedchest")) {
            if (gameRunning) {
                if (sender instanceof Player && this.dwarves.contains(((Player) sender).getPlayer())) {
                    Player player = (Player) sender;
                    if (player.getInventory().contains(Material.CHEST)) {
                        player.sendMessage("You already have a shared resource chest!");
                    }
                    else {
                        player.getInventory().addItem(DwarfItems.sharedResourceChest);
                        player.sendMessage(ChatColor.GOLD + "You have been given a " + ChatColor.AQUA + "shared resource chest" + ChatColor.GOLD + ".");
                    }
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("tutorial")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (this.tips.contains(player)) {
                    player.sendMessage(ChatColor.GOLD + "Tutorial mode has been disabled. You can re-enable it using /tutorial.");
                    this.tips.remove(player);
                }
                else if (!this.tips.contains(player)) {
                    player.sendMessage(ChatColor.GOLD + "Tutorial mode has been enabled. You can disable it using /tutorial.");
                    this.tips.add(player);
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("hats")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (this.dwarves.contains(player) || !this.gameRunning) {
                    player.openInventory(HatGUI.hatGUI);
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("setmonsterspawn")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                mobSpawn = player.getLocation();

                player.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Monster spawn has been set to: " + ChatColor.YELLOW + mobSpawn.getX() + ", " + mobSpawn.getY() + ", " + mobSpawn.getZ());
            }
        }
        else if (command.getName().equalsIgnoreCase("setdwarfspawn")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                dwarfSpawn = player.getLocation();

                player.sendMessage(ChatColor.GOLD + "[DvZ] " + ChatColor.WHITE + "Dwarf spawn has been set to: " + ChatColor.YELLOW + dwarfSpawn.getX() + ", " + dwarfSpawn.getY() + ", " + dwarfSpawn.getZ());
            }
        }
        return true;
    }

    private void setInventorySlots(Player player ) {
        player.getInventory().setItem(9, DwarfItems.inventorySlotHolder);
        player.getInventory().setItem(10, DwarfItems.inventorySlotHolder);
        player.getInventory().setItem(11, DwarfItems.inventorySlotHolder);
        player.getInventory().setItem(12, DwarfItems.inventorySlotHolder);
        player.getInventory().setItem(14, DwarfItems.inventorySlotHolder);
        player.getInventory().setItem(15, DwarfItems.inventorySlotHolder);
        player.getInventory().setItem(16, DwarfItems.inventorySlotHolder);
        player.getInventory().setItem(17, DwarfItems.inventorySlotHolder);
    }

    public void startGame() {
        this.gameRunning = true;
        this.gameStartTime = System.currentTimeMillis();

        if (this.startCommands != null) {
            for (String comm : this.startCommands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comm);
            }
        }

        initializeScoreboard();

        this.dwarfTeam = this.scoreboard.registerNewTeam("dwarf");
        this.monsterTeam = this.scoreboard.registerNewTeam("monster");

        this.dwarfTeam.setColor(ChatColor.DARK_AQUA);
        this.monsterTeam.setColor(ChatColor.DARK_RED);

        this.dwarfTeam.setAllowFriendlyFire(false);
        this.monsterTeam.setAllowFriendlyFire(false);

        for (Player player : Bukkit.getOnlinePlayers()) {
            barCountdown.removeBarFromPlayer(player);
            barCountdown.stopBarTimer();

            if (!player.hasPermission("game.ignore") && !rangerKit.contains(player) && !warriorKit.contains(player) && !paladinKit.contains(player)) {
                setAsDwarf(player);
            }
            else if (!player.hasPermission("game.ignore") && warriorKit.contains(player)) {
                setAsWarrior(player);
            }
            else if (!player.hasPermission("game.ignore") && paladinKit.contains(player)) {
                setAsPaladin(player);
            }
            else if (!player.hasPermission("game.ignore") && rangerKit.contains(player)) {
                setAsRanger(player);
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendTitle(ChatColor.BLUE + "It's time to play...", "", 10, 20, 10), 20L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendTitle(ChatColor.RED + "Dwarves " + ChatColor.WHITE + "vs" + ChatColor.DARK_GREEN + " Zombies", "", 20, 60, 30), 80L);

            if (this.tips.contains(player)) {
                player.sendMessage(ChatColor.GOLD + "====================[DvZ Tutorial]===================");
                player.sendMessage(ChatColor.YELLOW + "Welcome to Dwarves vs. Zombies! Your goal as a dwarf is to");
                player.sendMessage(ChatColor.YELLOW + "protect your shrines and kill monsters. When a shrine falls,");
                player.sendMessage(ChatColor.YELLOW + "you'll want to fall back to the next shrine. If you ever get");
                player.sendMessage(ChatColor.YELLOW + "lost, you can use /compass to see where everything is. Take a");
                player.sendMessage(ChatColor.YELLOW + "moment to see where everything is and gather the items you");
                player.sendMessage(ChatColor.YELLOW + "need. When night hits, a random selected group of dwarves will");
                player.sendMessage(ChatColor.YELLOW + "randomly die from a plague event and turn into monsters. As a");
                player.sendMessage(ChatColor.YELLOW + "monster, your goal is to kill all dwarves.");
                player.sendMessage(ChatColor.GOLD + "===================================================");
            }
        }
        int playerCount = Bukkit.getOnlinePlayers().size();
        if (this.specialDwarves != null && this.specialDwarves.size() > 0 && playerCount >= minPlayersForHeroes) {
            List<Player> list = new ArrayList<>(this.dwarves);

            for (String heroName : this.specialDwarves)
                list.remove(heroName);

            Iterator<Player> iter = list.iterator();
            while (iter.hasNext()) {
                Player player = Bukkit.getPlayerExact(iter.next().getPlayer().getName());
                if (player == null) {
                    iter.remove();
                    continue;
                }
            }
            if (list.size() < 3) {
                list = new ArrayList<>(this.dwarves);
                for (String heroName : this.specialDwarves)
                    list.remove(heroName);
            }
            String first = this.specialDwarves.remove(0);
            Collections.shuffle(this.specialDwarves);
            this.specialDwarves.add(0, first);

            int totalSpecialDwarves = 3;
            int specialDwarvesCreated = 0;

            for (int i = 0; i < this.specialDwarves.size(); i++) {
                String heroName = this.specialDwarves.get(i);
                Player player = Bukkit.getPlayerExact(heroName);

                if (player == null) {
                    if (player == null && specialDwarvesCreated < totalSpecialDwarves && list.size() > 0) {
                        Player randomPlayer = list.remove(this.random.nextInt(list.size()));
                        player = Bukkit.getPlayerExact(randomPlayer.getName());
                    }
                    if (player != null && player.isValid()) {
                        Spell spell = MagicSpells.getSpellByInternalName("become_" + heroName);
                        if (spell != null) {
                            spell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);
                            this.heroes.put(player, heroName);
                            specialDwarvesCreated++;
                        }
                    }
                }
            }
        }

        final BukkitTask timer = new BukkitRunnable() {
            public void run() {
                if (!gameEnded) {
                    totalTime++;
                }
                else {
                    cancel();
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        this.shrinePower = 200.D;

        if (this.monsterReleaseTime > 0 && !this.override && this.monsterReleaseCommands != null && this.monsterReleaseCommands.size() > 0) {
            this.monsterReleaseTask = Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                public void run() {
                    DvZ.this.beginMonsterRelease();
                }
            }, (this.monsterReleaseTime * 20));
            this.counterTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                public void run() {
                    DvZ.this.recount();
                }
            }, (this.scoreInterval * 20), (this.scoreInterval * 20));
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for (Player player : dwarves) {
                    if (!player.getInventory().contains(Material.ARROW, 64)) {
                        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                    }
                    else if (player.getInventory().contains(Material.ARROW, 0)) {
                        player.getInventory().setItem(13, new ItemStack(Material.ARROW, 1));
                    }
                    else {
                        return;
                    }
                }
            }
        },  0L, 200L);

        this.bleeder = new Bleeder(this);

        if (this.shrineBarManager != null) {
            this.shrineBarManager.sendBarToAllPlayers();
        }
    }

    public void setAsDwarf(Player player) {
        this.becomeDwarfSpell.cast(player, 1.0F, null);

        this.dwarves.add(player.getPlayer());
        dwarfTeam.addEntry(player.getName());
        setInventorySlots(player);

        player.getInventory().setItem(13, new ItemStack(Material.ARROW));
        player.teleport(dwarfSpawn);
    }

    public void setAsWarrior(Player player) {
        Spell warriorSpell = MagicSpells.getSpellByInternalName("warrior_become_warrior");
        warriorSpell.cast(player, 1.0F, null);

        this.dwarves.add(player.getPlayer());
        dwarfTeam.addEntry(player.getName());
        setInventorySlots(player);

        player.getInventory().setItem(13, new ItemStack(Material.ARROW));
        player.teleport(dwarfSpawn);
    }

    public void setAsPaladin(Player player) {
        Spell paladinSpell = MagicSpells.getSpellByInternalName("paladin_become_paladin");
        paladinSpell.cast(player, 1.0F, null);

        this.dwarves.add(player);
        dwarfTeam.addEntry(player.getName());
        setInventorySlots(player);

        player.getInventory().setItem(13, new ItemStack(Material.ARROW));
        player.teleport(dwarfSpawn);
    }

    public void setAsRanger(Player player) {
        Spell rangerSpell = MagicSpells.getSpellByInternalName("ranger_become_ranger");
        rangerSpell.cast(player, 1.0F, null);

        this.dwarves.add(player.getPlayer());
        dwarfTeam.addEntry(player.getName());
        setInventorySlots(player);

        player.getInventory().setItem(13, new ItemStack(Material.ARROW));
        player.teleport(dwarfSpawn);
    }

    public void setAsCharles(Player player) {
        Spell heroSpell = MagicSpells.getSpellByInternalName("become_charles");
        heroSpell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);

        this.dwarves.add(player.getPlayer());
        dwarfTeam.addEntry(player.getName());
        setInventorySlots(player);

        player.getInventory().setItem(13, new ItemStack(Material.ARROW));
        player.teleport(dwarfSpawn);
    }

    public void setAsMarsh(Player player) {
        Spell heroSpell = MagicSpells.getSpellByInternalName("become_marsh");
        heroSpell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);

        this.dwarves.add(player.getPlayer());
        dwarfTeam.addEntry(player.getName());
        setInventorySlots(player);

        player.getInventory().setItem(13, new ItemStack(Material.ARROW));
        player.teleport(dwarfSpawn);
    }

    public void setAsApollo(Player player) {
        Spell heroSpell = MagicSpells.getSpellByInternalName("become_apollo");
        heroSpell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);

        this.dwarves.add(player.getPlayer());
        dwarfTeam.addEntry(player.getName());
        setInventorySlots(player);

        player.getInventory().setItem(13, new ItemStack(Material.ARROW));
        player.teleport(dwarfSpawn);
    }

    private void beginMonsterRelease() {
        if (this.monstersReleased) {
            return;
        }
        System.out.println("BEGIN MONSTER RELEASE");

        this.monstersReleased = true;
        if (!this.override) {
            this.gameEvent.run();
        }
        else {
            releaseMonsters();
        }
    }

    public void releaseMonsters() {
        for (String comm : this.monsterReleaseCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comm);
        }
        startMonsterSpecialTask();

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.playSound(players.getLocation(), "brucemonsters", 0.3F, 1F);
        }

        this.shrineManager.startPulse();
        this.shrineManager.decreaseShrineHealth();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                DvZ.this.spawnZombies(15);
            }
        },  200L, 50L);

        this.doomScore = this.objective.getScore(ChatColor.DARK_RED + "Doom Clock");
        this.doomScore.setScore(doomTimer);

        this.monstersReleasedFully = true;

        final BukkitTask doomTimerCountdown = new BukkitRunnable() {
            public void run() {
                if (monstersReleasedFully) {
                    if (doomTimer != -1) {
                        doomTimer--;

                        if (doomTimer <= 0) {
                            doomTimer = 0;

                            doomEventStart();
                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        System.out.println("END MONSTER RELEASE");
    }

    Set<Monster> aiMonsters = new HashSet<>();
    List<String> aiMonsterNames = new ArrayList<>();
    private void spawnZombies(int count) {
        int dwarvesRemaining = this.remainingDwarvesScore.getScore();
        int aiMonsterCap = (int)Math.round(dwarvesRemaining * 1.5D);

        for (Player players : this.monsters) {
            aiMonsterNames.add(players.getName());
        }

        if (dwarvesRemaining < 1)
            return;

        Iterator<Monster> iter = this.aiMonsters.iterator();
        while (iter.hasNext()) {
            Monster m = iter.next();

            if (m.isDead()) {
                iter.remove();
                continue;
            }
            if (m.getTicksLived() > 800) {
                m.setHealth(0);
                iter.remove();
            }
        }
        if (this.aiMonsters.size() >= aiMonsterCap)
            return;

        List<Player> targets = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isDead() && this.monsters.contains(p) && p.getLocation().distanceSquared(this.mapSpawn) > 10000.0D)
                targets.add(p);
        }
        Collections.shuffle(targets);

        for (int i = 0; i < count && targets.size() > 0 && this.aiMonsters.size() < aiMonsterCap; i++) {
            Player target = targets.get(this.random.nextInt(targets.size()));
            Location loc = target.getLocation();

            for (int attempts = 0; attempts < 10; attempts++) {
                loc = loc.clone().add((this.random.nextInt(40) - 20), 1.0D, (this.random.nextInt(40) - 20));

                if (BlockUtils.isPathable(loc.getBlock()) && BlockUtils.isPathable(loc.getBlock().getRelative(BlockFace.UP))) {
                    Block block = loc.getBlock();

                    while (!BlockUtils.isPathable(block))
                        block = block.getRelative(BlockFace.DOWN);

                    loc = block.getLocation().add(0.5D, 0.5D, 0.5D);
                    break;
                }
                int c = 0;
                Block b = loc.getBlock();

                while (!BlockUtils.isPathable(b) && !BlockUtils.isPathable(b.getRelative(BlockFace.UP)) && c < 10) {
                    b = b.getRelative(BlockFace.UP);
                    c++;
                }
                if (BlockUtils.isPathable(loc.getBlock()) && BlockUtils.isPathable(loc.getBlock().getRelative(BlockFace.UP))) {
                    loc = b.getLocation().add(0.5D, 0.5D, 0.5D);
                    break;
                }
            }
            if (dwarvesRemaining > 10 && this.shrineManager.atFinalShrine() && loc.getWorld().getHighestBlockYAt(loc) > loc.getY() + 2.0D) {
                Creeper creeper = loc.getWorld().spawn(loc, Creeper.class);

                creeper.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false));
                creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false));

                if (this.aiMonsterNames != null && this.aiMonsterNames.size() > 25) {
                    creeper.setCustomName(ChatColor.DARK_RED + this.aiMonsterNames.get(this.random.nextInt(this.aiMonsterNames.size())));
                    creeper.setCustomNameVisible(false);
                }
                this.aiMonsters.add(creeper);
            }
            else {
                AttributeModifier followRange = new AttributeModifier("followRange", 40D, AttributeModifier.Operation.ADD_NUMBER);
                AttributeModifier attackDamage = new AttributeModifier("attackDamage", 2D, AttributeModifier.Operation.ADD_NUMBER);

                Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);
                MagicSpells.getAttributeManager().addEntityAttribute(zombie, Attribute.GENERIC_FOLLOW_RANGE, followRange);
                MagicSpells.getAttributeManager().addEntityAttribute(zombie, Attribute.GENERIC_ATTACK_DAMAGE, attackDamage);

                zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2, false));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false));

                zombie.getEquipment().setItemInMainHand(new ItemStack(Material.CLAY_BALL));
                zombie.getEquipment().setItemInMainHandDropChance(0.0F);

                zombie.getEquipment().setBoots(null);
                zombie.getEquipment().setLeggings(null);
                zombie.getEquipment().setChestplate(null);
                zombie.getEquipment().setHelmet(HatItems.santaHat);

                if (this.aiMonsterNames != null && this.aiMonsterNames.size() > 25) {
                    zombie.setCustomName(ChatColor.DARK_RED + this.aiMonsterNames.get(this.random.nextInt(this.aiMonsterNames.size())));
                    zombie.setCustomNameVisible(false);
                }
                this.aiMonsters.add(zombie);
            }
        }
    }

    private void startMonsterSpecialTask() {
        if (this.monsterSpecialInterval > 0 && this.monsterSpecialSpell != null) {
            this.monsterSpecialTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                public void run() {
                    DvZ.this.runMonsterSpecial();
                }
            }, (this.monsterSpecialInterval * 20), (this.monsterSpecialInterval * 20));
        }
    }

    public void initializeScoreboard() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            this.manager = Bukkit.getScoreboardManager();
            this.scoreboard = manager.getMainScoreboard();
            this.objective = this.scoreboard.getObjective("Dwarves");

            if (this.objective == null) {
                this.objective = this.scoreboard.registerNewObjective("Dwarves", "dummy", ChatColor.AQUA + "Dwarves");
                this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }

            this.vaultScore = this.objective.getScore(ChatColor.GOLD + "Vault");
            this.vaultScore.setScore(totalVaultAmount);
            this.remainingDwarvesScore = this.objective.getScore(ChatColor.GREEN + "Remaining");
            this.remainingDwarvesScore.setScore(dwarves.size());
            this.timeScore = this.objective.getScore(ChatColor.LIGHT_PURPLE + "Time");
            this.timeScore.setScore(totalTime);

            players.setScoreboard(scoreboard);
        }

        updateScoreboardTask = new BukkitRunnable() {
            public void run() {
                updateScoreboards();
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    private void doomEventTimer() {
        final BukkitTask doomTimerCountdown = new BukkitRunnable() {
            public void run() {
                if (monstersReleasedFully) {
                    if (doomTimer != -1) {
                        doomTimer--;

                        if (doomTimer <= 0) {
                            doomTimer = 0;

                            doomEventStart();
                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private void doomEventStart() {
        final BukkitTask doomTimerSound = new BukkitRunnable() {
            public void run() {
                if (doomTimerAfter != -1) {
                    doomTimerAfter--;

                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (doomTimerAfter == 10) {
                            players.playSound(players.getLocation(), "drum", 1, 1);
                        }
                        if (doomTimerAfter == 8) {
                            players.playSound(players.getLocation(), "drum", 1, 1);
                        }
                        if (doomTimerAfter == 6) {
                            players.playSound(players.getLocation(), "drum", 1, 1);
                        }
                        if (doomTimerAfter == 4) {
                            players.playSound(players.getLocation(), "drum", 1, 1);
                        }
                        if (doomTimerAfter == 2) {
                            players.playSound(players.getLocation(), "drum", 1, 1);
                        }
                        if (doomTimerAfter == 0) {
                            players.playSound(players.getLocation(), "drum", 1, 1);
                        }
                    }

                    if (doomTimerAfter <= 0) {
                        doomTimerAfter = 0;

                        Random randomDoomEvent = new Random();
                        int randomDoom = randomDoomEvent.nextInt(20);

                        if (randomDoom < 5) {
                            DvZ.this.doomEvent = new GolemEvent(DvZ.this);
                        }
                        else if (randomDoom < 10) {
                            DvZ.this.doomEvent = new DireWolvesEvent(DvZ.this);
                        }
                        else if (randomDoom < 15) {
                            DvZ.this.doomEvent = new GoblinSquadEvent(DvZ.this);
                        }

                        doomEvent.run();

                        for (Player players : Bukkit.getOnlinePlayers())
                            Bukkit.getScheduler().runTaskLater(plugin, () -> players.sendTitle(ChatColor.RED + doomEvent.getName(), "", 20, 60, 30), 20L);

                        doomTimer = 1000;
                        doomEventTimer();
                        cancel();
                        //doomTimerAfter = 20;
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    public void updateScoreboards() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            this.vaultScore = this.objective.getScore(ChatColor.GOLD + "Vault");
            this.vaultScore.setScore(totalVaultAmount);
            this.remainingDwarvesScore = this.objective.getScore(ChatColor.GREEN + "Remaining");
            this.remainingDwarvesScore.setScore(dwarves.size());
            this.monsterKillsScore = this.objective.getScore(ChatColor.RED + "Kills");
            this.monsterKillsScore.setScore(monsterKills);

            if (monstersReleasedFully) {
                this.doomScore = this.objective.getScore(ChatColor.DARK_RED + "Doom Clock");
                this.doomScore.setScore(doomTimer);
            }
            this.timeScore = this.objective.getScore(ChatColor.LIGHT_PURPLE + "Time");
            this.timeScore.setScore(totalTime);
        }
    }

    public void recount() {
        double shrinePowerMod = this.shrineBarManager.health;
        int c = 0;

        double dwarfValue = this.shrineManager.getDwarfValue();
        double monsterValue = this.shrineManager.getMonsterValue();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isDead()) {
                if (this.dwarves.contains(p.getPlayer())) {
                    c++;
                    if (this.shrineManager.playerNearShrineForCapture(p)) {
                        shrinePowerMod += dwarfValue;
                        shrinePower += dwarfValue;
                    }
                }
                else if (this.monsters.contains(p.getPlayer()) && this.shrineManager.playerNearShrineForCapture(p)) {
                    shrinePowerMod -= monsterValue;
                    shrinePower -= monsterValue;
                }
            }
        }

        if (this.remainingDwarvesScore.getScore() != c) {
            this.remainingDwarvesScore.setScore(c);
        }

        if (this.shrinePower < 0.0D || this.shrineManager.shouldRestore()) {
            this.shrinePower += shrinePowerMod;
        }

        if (this.shrinePower > this.shrineManager.getMaxShrinePower()) {
            this.shrinePower = this.shrineManager.getMaxShrinePower();
        }
        else if (this.shrinePower < 0.0D) {
            this.shrinePower = 0.0D;
        }

        if (c == 0 || this.shrinePower == 0.0D) {
            String currentShrineName = this.shrineManager.getCurrentShrineName();
            boolean gameEnded = this.shrineManager.destroyCurrentShrine();

            if (gameEnded) {
                endGame();
            }
            else {
                //ShrineDestroyedListener.initialize("");
                this.shrineBarManager.health = 200.0D;
                this.shrinePower = 200.0D;

                if (this.shrineBarManager != null) {
                    this.shrineBarManager.setBarName(this.shrineManager.getCurrentShrineName() + " (" + (DvZ.plugin.shrineManager.currentShrine+1) + "/" + DvZ.plugin.shrineManager.shrines.size() + ")");
                }

                Bukkit.broadcastMessage(ChatColor.GOLD + "=================================================");
                Bukkit.broadcastMessage(ChatColor.YELLOW + "THE " + currentShrineName.toUpperCase() + " HAS FALLEN!");
                Bukkit.broadcastMessage(ChatColor.GOLD + "=================================================");
            }
        }
        else if (this.endTask == null && this.startEndTimerAtPercent > 0 && c < Math.round((Bukkit.getOnlinePlayers()).size() * this.startEndTimerAtPercent / 100.0F)) {
            this.endTask = Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                public void run() {
                    DvZ.this.endGame();
                }
            }, (this.endTimerDuration * 20));
        }
    }

    public void endGame() {
        if (this.gameRunning) {
            this.gameRunning = false;
            this.gameEnded = true;

            Bukkit.broadcastMessage(ChatColor.RED + "=================================================");
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "THE FINAL DWARVEN SHRINE HAS FALLEN!");
            Bukkit.broadcastMessage(ChatColor.RED + "=================================================");

            dwarfTeam.unregister();
            monsterTeam.unregister();

            if (this.counterTask != null) {
                this.counterTask.cancel();
                this.counterTask = null;
            }
            if (this.endTask != null) {
                this.endTask.cancel();
                this.endTask = null;
            }
            if (this.monsterSpecialTask != null) {
                this.monsterSpecialTask.cancel();
                this.monsterSpecialTask = null;
            }

            if (this.shrineBarManager != null) {
                this.shrineBarManager.removeBarForAllPlayers();
            }

            for (Player playerName : this.monsters) {
                Player player = Bukkit.getPlayerExact(playerName.getName());
                if (player != null && player.isValid()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 4, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 1, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6000, 2, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 0, true));
                }
            }

            Location landSpot = this.shrineManager.getFinalShrine().clone();
            while (landSpot.getBlock().getType() != Material.AIR) {
                landSpot.add(0.0D, 1.0D, 0.0D);
            }

            for (Player playerName : this.dwarves) {
                Player player = Bukkit.getPlayerExact(playerName.getName());
                if (player != null && player.isValid()) {
                    player.teleport(landSpot);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 6000, 1, true));
                    player.getInventory().setArmorContents(null);
                }
            }
        }
    }

    private void runMonsterSpecial() {
        if (this.monsterSpecialSpell != null) {
            System.out.println("SPECIAL MONSTER TIME!");

            for (int i = 0; i < 15; i++) {
                List<Player> list = new ArrayList<>(this.monsters);
                System.out.println("Choosing random number between 0 and " + list.size());

                int r = this.random.nextInt(list.size());

                Player playerName = list.get(r);
                System.out.println("Chose " + r + ", which is " + playerName);
                Player player = Bukkit.getPlayerExact(playerName.getPlayer().getName());

                if (player != null && player.isValid()) {
                    System.out.println("All good, running special...");
                    this.monsterSpecialSpell.cast(player, 1.0F, null);
                    break;
                }
                System.out.println("Invalid player.");
            }
        }
    }
}