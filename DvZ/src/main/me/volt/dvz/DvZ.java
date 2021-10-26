package main.me.volt.dvz;

import com.nisovin.magicspells.util.managers.PassiveManager;
import main.me.volt.dvz.conditions.*;
import main.me.volt.dvz.events.doom.DireWolvesEvent;
import main.me.volt.dvz.events.doom.DoomEvent;
import main.me.volt.dvz.events.doom.GolemEvent;
import me.volt.main.mcevolved.GameMode;
import me.volt.main.mcevolved.MCEvolved;

import main.me.volt.dvz.events.PlagueEvent;
import main.me.volt.dvz.passives.ShrineDestroyedListener;
import main.me.volt.dvz.utils.Bleeder;
import main.me.volt.dvz.events.RageEvent;
import main.me.volt.dvz.listeners.GameListener;
import main.me.volt.dvz.managers.ShrineManager;
import main.me.volt.dvz.utils.ShrineBarManager;
import main.me.volt.dvz.events.GameEvent;

import com.nisovin.magicspells.util.managers.ConditionManager;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.BlockUtils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.util.*;

public class DvZ extends JavaPlugin implements GameMode, Listener {
    public static DvZ plugin;
    
    public boolean gameRunning = false;
    public boolean gameEnded = false;

    public boolean monstersReleased = false;
    public boolean monstersReleasedFully = false;

    public boolean override = false;

    public long gameStartTime = 0L;
    public int gameStartAttempts = 0;

    public String mapName;

    public int autoStartTime;
    public int monsterReleaseTime;

    public int minPlayers;

    public int scoreInterval;
    public int percentMonsters;

    public int monsterSpecialInterval;

    public boolean killDwarvesOnJoin;

    public int startEndTimerAtPercent;
    public int endTimerDuration;

    public Spell becomeDwarfSpell;
    public Spell becomeMonsterSpell;
    public Spell monsterSpecialSpell;

    public List<String> startCommands;
    public List<String> monsterReleaseCommands;

    public List<String> specialDwarves;

    public Location mapSpawn;

    public ShrineManager shrineManager;
    public double shrinePower = 100.0D;

    public Set<Player> dwarves;
    public Set<Player> monsters;

    ScoreboardManager manager;
    Scoreboard scoreboard;
    Objective objective;

    public Score remainingDwarvesScore;
    public Score monsterKillsScore;
    public int monsterKills = 0;
    public Score timeScore;
    private int totalTime = 0;
    public Score doomScore;
    private int doomTimer = 10;

    BukkitTask counterTask;
    BukkitTask monsterReleaseTask;
    BukkitTask endTask;
    BukkitTask monsterSpecialTask;

    private Bleeder bleeder;
    public GameEvent gameEvent;
    public DoomEvent doomEvent;
    public ShrineBarManager volatileCode;

    public Random random = new Random();

    private ConditionManager conditionManager = new ConditionManager();
    private PassiveManager passiveManager = new PassiveManager();

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
        conditionManager.addCondition("shrinepowerlessthan", ShrinePowerLessThanCondition.class);
        conditionManager.addCondition("shrinedistancelessthan", ShrineDistanceLessThanCondition.class);
        conditionManager.addCondition("shrinedistancemorethan", ShrineDistanceMoreThanCondition.class);

        passiveManager.addListener("shrinedestroyed", ShrineDestroyedListener.class);
    }

    public void onEnable() {
        if (gameRunning) {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    this.initializeScoreboard();
                }
            }
        }

        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            saveDefaultConfig();
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

        Bukkit.getWorlds().get(0).setDifficulty(Difficulty.HARD);

        Bukkit.getWorlds().get(0).setStorm(false);
        Bukkit.getWorlds().get(0).setWeatherDuration(300000);

        Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_TILE_DROPS, false);
        Bukkit.getWorlds().get(0).setGameRule(GameRule.SPAWN_RADIUS, 1);

        this.mapName = config.getString("map-name", "map");
        this.autoStartTime = config.getInt("auto-start-time", 0);

        this.minPlayers = config.getInt("min-players", 0);

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

        if (mapSpawnString.isEmpty()) {
            this.mapSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
        }
        else {
            String[] split = mapSpawnString.split(",");
            this.mapSpawn = new Location(Bukkit.getWorlds().get(0), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }

        this.shrineManager = new ShrineManager(this, config);

        this.dwarves = Collections.synchronizedSet(new HashSet<>());
        this.monsters = Collections.synchronizedSet(new HashSet<>());

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

        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        this.volatileCode = new ShrineBarManager(this.shrineManager.getCurrentShrineName() + " Power");

        int r = this.random.nextInt(15);

        if (r < 1) {
            this.gameEvent = new PlagueEvent(this);
        }
        else if (r < 15) {
            this.gameEvent = new RageEvent(this);
        }

        MCEvolved.initializeGameMode(this);
    }

    public void onDisable() {
        endGame();
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("startgame")) {
            if (!this.gameRunning && !this.gameEnded) {
                startGame();
                MCEvolved.stopCountdown();
                sender.sendMessage("Game started.");
            }
            else {
                sender.sendMessage("Game has already started.");
            }
        }
        else if (command.getName().equalsIgnoreCase("override")) {
            if (!this.gameRunning) {
                this.override = true;
                sender.sendMessage("Override mode enabled. You will need to manually /startgame and /releasemonsters.");
            }
            else if (!this.monstersReleased) {
                if (this.monsterReleaseTask != null) {
                    this.monsterReleaseTask.cancel();
                }
                this.override = true;
                sender.sendMessage("Override mode enabled. You will need to manually /releasemonsters.");
            }
            else {
                sender.sendMessage("The game has already started and monsters have already been released.");
            }
        }
        else if (command.getName().equalsIgnoreCase("event")) {
            PlagueEvent plagueEvent = new PlagueEvent(this);
            if (args.length == 0) {
                sender.sendMessage("Active event: " + this.gameEvent.getName());
                if (this.override) {
                    sender.sendMessage("Except the game is overwritten!");
                }
                return true;
            }
            if (this.monstersReleased) {
                sender.sendMessage("The monsters are already released.");
                return true;
            }

            GameEvent event = null;
            if (args[0].equalsIgnoreCase("rage")) {
                RageEvent rageEvent = new RageEvent(this);
            }
            else if (args[0].equalsIgnoreCase("plague")) {
                plagueEvent = new PlagueEvent(this);
            }
            else {
                sender.sendMessage("Usage: / event [rage|plague]");
                return true;
            }

            if (!this.gameRunning || !this.override) {
                this.gameEvent = plagueEvent;
                sender.sendMessage("Game event set to " + this.gameEvent.getName());
            }
            else {
                this.gameEvent = plagueEvent;
                this.gameEvent.run();
                this.monstersReleased = true;

                sender.sendMessage("Game event set to " + this.gameEvent.getName() + " and activated.");
            }
        }
        else if (command.getName().equalsIgnoreCase("releasemonsters")) {
            if (this.gameRunning) {
                if (!this.monstersReleased) {
                    beginMonsterRelease();
                    sender.sendMessage("Monsters released.");
                } else {
                    sender.sendMessage("Monsters have already been released.");
                }
            } else {
                sender.sendMessage("Game hasn't started.");
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
                sender.sendMessage("Usage: /setdwarf playername");
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage("No player found");
                }
                else {
                    this.gameEvent.setDwarf(player);
                    this.monsters.remove(player.getPlayer());
                    this.dwarves.add(player.getPlayer());
                    this.becomeDwarfSpell.cast(player, 1.0F, null);
                    sender.sendMessage("Player " + player.getName() + " is now a dwarf.");
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("setmonster")) {
            if (args.length != 1) {
                sender.sendMessage("Usage: /setmonster playername");
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage("No player found");
                }
                else {
                    this.dwarves.remove(player.getPlayer());
                    this.monsters.add(player.getPlayer());
                    this.becomeMonsterSpell.cast(player, 1.0F, null);
                    sender.sendMessage("Player " + player.getName() + " is now a monster.");
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("sethero")) {
            if (gameRunning) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null) {
                    sender.sendMessage("No player found");
                }
                else {
                    sender.sendMessage("Player " + player.getName() + " is now a hero.");
                }

                if (args[1].equalsIgnoreCase("volt")){
                    setAsVoltHero(player);
                }
                else if (args[1].equalsIgnoreCase("kamchatka")) {
                    setAsKamchatkaHero(player);
                }
                else if (args[1].equalsIgnoreCase("blu")) {
                    setAsBlueHero(player);
                }
            }
            else if (args.length != 2) {
                sender.sendMessage("Usage: /sethero playername hero");
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
                sender.sendMessage("Special monster interval set to " + this.monsterSpecialInterval + " seconds.");
            }
            else {
                sender.sendMessage("Invalid interval.");
            }
        }
        return true;
    }

    public void startGame() {
        this.gameRunning = true;
        this.gameStartTime = System.currentTimeMillis();

        if (this.startCommands != null) {
            for (String comm : this.startCommands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comm);
            }
        }


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("game.ignore")) {
                setAsDwarf(player);
            }
            initializeScoreboard();

            Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendTitle(ChatColor.BLUE + "It's time to play...", "", 10, 20, 10), 20L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.sendTitle(ChatColor.RED + "Dwarves " + ChatColor.WHITE + "vs" + ChatColor.DARK_GREEN + " Zombies", "", 20, 60, 30), 80L);
        }

        this.shrinePower = 100.D;

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

        this.bleeder = new Bleeder(this);

        if (this.volatileCode != null) {
            this.volatileCode.sendBarToAllPlayers();
        }
    }

    public void setAsDwarf(Player player) {
        this.becomeDwarfSpell.cast(player, 1.0F, null);
        this.dwarves.add(player.getPlayer());
    }

    public void setAsVoltHero(Player player) {
        Spell heroSpell = MagicSpells.getSpellByInternalName("become_volt_real");
        heroSpell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);
        this.dwarves.add(player.getPlayer());
    }

    public void setAsKamchatkaHero(Player player) {
        Spell heroSpell = MagicSpells.getSpellByInternalName("become_kamchatka_real");
        heroSpell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);
        this.dwarves.add(player.getPlayer());
    }

    public void setAsBlueHero(Player player) {
        Spell heroSpell = MagicSpells.getSpellByInternalName("become_blu_real");
        heroSpell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);
        this.dwarves.add(player.getPlayer());
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

        this.shrineManager.startPulse();
        this.shrineManager.decreaseShrineHealth();

        this.doomScore = this.objective.getScore(ChatColor.DARK_RED + "Doom Clock");
        this.doomScore.setScore(doomTimer);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                DvZ.this.spawnZombies(15);
            }
        }, 200L, 50L);

        this.monstersReleasedFully = true;
        System.out.println("END MONSTER RELEASE");
    }

    Set<Monster> aiMonsters = new HashSet<>();
    List<String> aiMonsterNames = new ArrayList<>();
    public void spawnZombies(int count) {
        int dwarvesRemaining = this.remainingDwarvesScore.getScore();
        int aiMonsterCap = (int)Math.round(dwarvesRemaining * 1.5D);

        if (dwarvesRemaining < 1) {
            return;
        }

        if (dwarvesRemaining < 3) {
            count /=2;
        }

        Iterator<Monster> iter = this.aiMonsters.iterator();
        while (iter.hasNext()) {
            Monster m = iter.next();
            if (m.isDead()) {
                continue;
            }
            if (m.getTicksLived() > 600) {
                m.setHealth(0);
                iter.remove();
            }
        }
        if (this.aiMonsters.size() >= aiMonsterCap) {
            return;
        }
        List<Player> targets = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isDead() && this.monsters.contains(p.getPlayer()) && p.getLocation().distanceSquared(this.mapSpawn) > 10000.0D) {
                targets.add(p);
            }
        }
        Collections.shuffle(targets);

        for (int i = 0; i < count && targets.size() > 0 && this.aiMonsters.size() < aiMonsterCap; i++) {
            Player target = targets.get(this.random.nextInt(targets.size()));
            Location loc = target.getLocation();

            for (int attempts = 0; attempts < 10; attempts++) {
                loc = loc.clone().add((this.random.nextInt(40) - 20), 1.0D, (this.random.nextInt(40) - 20));
                if (BlockUtils.isPathable(loc.getBlock()) && BlockUtils.isPathable(loc.getBlock().getRelative(BlockFace.UP))) {
                    Block block = loc.getBlock();
                    while (!BlockUtils.isPathable(block)) {
                        block = block.getRelative(BlockFace.DOWN);
                    }
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
            if (dwarvesRemaining > 20 && this.shrineManager.atFinalShrine() && loc.getWorld().getHighestBlockYAt(loc) > loc.getY() + 2.0D) {
                Creeper creeper = loc.getWorld().spawn(loc, Creeper.class);
                creeper.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 3, true));
                creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 3, true));

                if (this.aiMonsterNames != null && this.aiMonsterNames.size() > 25) {
                    creeper.setCustomName(ChatColor.DARK_RED + this.aiMonsterNames.get(this.random.nextInt(this.aiMonsterNames.size())));
                    creeper.setCustomNameVisible(true);
                }
                this.aiMonsters.add(creeper);
            }
            else {
                Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);
                //MagicSpells.getAttributeManager().addEntityAttribute(zombie, Attribute.GENERIC_FOLLOW_RANGE, null);
                //MagicSpells.getAttributeManager().addEntityAttribute(zombie, Attribute.GENERIC_ATTACK_DAMAGE, null);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 3, true));
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 3, true));

                zombie.getEquipment().setItemInMainHand(new ItemStack(Material.CLAY_BALL));
                zombie.getEquipment().setItemInMainHandDropChance(0.0F);
                zombie.getEquipment().setBoots(null);
                zombie.getEquipment().setLeggings(null);
                zombie.getEquipment().setChestplate(null);
                zombie.getEquipment().setHelmet(null);

                if (this.aiMonsterNames != null && this.aiMonsterNames.size() > 25) {
                    zombie.setCustomName(ChatColor.DARK_RED + this.aiMonsterNames.get(this.random.nextInt(this.aiMonsterNames.size())));
                    zombie.setCustomNameVisible(true);
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

            this.remainingDwarvesScore = this.objective.getScore(ChatColor.GREEN + "Remaining");
            this.remainingDwarvesScore.setScore(dwarves.size());
            this.timeScore = this.objective.getScore(ChatColor.LIGHT_PURPLE + "Time");
            this.timeScore.setScore(totalTime);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                public void run() {
                    if (monstersReleasedFully) {
                        if (doomTimer != -1) {
                            doomTimer--;

                            if (doomTimer <= 0) {
                                doomTimer = 0;

                                Random randomDoomEvent = new Random();
                                int randomDoom = randomDoomEvent.nextInt(25);

                                if (randomDoom < 10) {
                                    DvZ.this.doomEvent = new GolemEvent(DvZ.this);
                                }
                                else if (randomDoom < 20) {
                                    DvZ.this.doomEvent = new DireWolvesEvent(DvZ.this);
                                }

                                doomEvent.run();
                                Bukkit.getScheduler().runTaskLater(plugin, () -> players.sendTitle(ChatColor.RED + doomEvent.getName(), "", 20, 60, 30), 20L);

                                doomTimer = 1200;
                            }
                        }
                    }
                    updateScoreboards();
                }
            }, 20L, 20L);

            players.setScoreboard(scoreboard);
        }
    }

    public void updateScoreboards() {
        for (Player online : Bukkit.getOnlinePlayers()) {
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

    public void updateScoreboard() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            public void run() {
                DvZ.this.updateScoreboards();
            }
        }, 0L, 100L);
    }

    public void recount() {
        double shrinePowerMod = 0.0D;
        int c = 0;

        double dwarfValue = this.shrineManager.getDwarfValue();
        double monsterValue = this.shrineManager.getMonsterValue();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isDead()) {
                if (this.dwarves.contains(p.getPlayer())) {
                    c++;
                    if (this.shrineManager.playerNearShrineForCapture(p)) {
                        shrinePowerMod += dwarfValue;
                    }
                }
                else if (this.monsters.contains(p.getPlayer()) && this.shrineManager.playerNearShrineForCapture(p)) {
                    shrinePowerMod -= monsterValue;
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
                this.shrinePower = 100.0D;

                if (this.volatileCode != null) {
                    this.volatileCode.setBarName(this.shrineManager.getCurrentShrineName() + " Power");
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

        if (shrinePowerMod != 0.0D && this.volatileCode != null)
            this.volatileCode.changeBarHealth((int)Math.floor(this.shrinePower * 2.0D));
    }

    public void endGame() {
        if (this.gameRunning) {
            this.gameRunning = false;
            this.gameEnded = true;

            Bukkit.broadcastMessage(ChatColor.RED + "=================================================");
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "THE FINAL DWARVEN SHRINE HAS FALLEN!");
            Bukkit.broadcastMessage(ChatColor.RED + "=================================================");

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

            if (this.volatileCode != null) {
                this.volatileCode.removeBarForAllPlayers();
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
            while(landSpot.getBlock().getType() != Material.AIR) {
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

            objective.getScoreboard().resetScores("Dwarves");
        }
    }

    private void runMonsterSpecial() {
        if (this.monsterSpecialSpell != null) {
            System.out.println("SPECIAL MONSTER TIME!");

            for (int i = 0; i < 15; i++) {
                List<String> list = new ArrayList(this.monsters);
                System.out.println("Choosing random number between 0 and " + list.size());

                int r = this.random.nextInt(list.size());

                String playerName = list.get(r);
                System.out.println("Chose " + r + ", which is " + playerName);
                Player player = Bukkit.getPlayerExact(playerName);

                if (player != null && player.isValid()) {
                    System.out.println("All good, running special...");
                    this.monsterSpecialSpell.castSpell(player, Spell.SpellCastState.NORMAL, 1.0F, null);
                    break;
                }
                System.out.println("Invalid player.");
            }
        }
    }
}
