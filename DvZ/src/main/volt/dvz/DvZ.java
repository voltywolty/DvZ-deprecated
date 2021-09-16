package main.volt.dvz;

import main.volt.dvz.commands.ClassCommands;
import main.volt.dvz.commands.CommandManager;
import main.volt.dvz.events.ClassEvents;
import main.volt.dvz.events.MonsterEvents;
import main.volt.dvz.events.PlayerEvents;
import main.volt.dvz.items.ItemManager;
import main.volt.dvz.items.MonsterItemManager;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DvZ extends JavaPlugin {
	private static DvZ Instance;
	public CommandManager commandManager;
	
	@Override
	public void onEnable() {
		setInstance(this);
		
		loadConfig();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[DvZ]: Plugin is enabled!");
		
		commandManager = new CommandManager();
		commandManager.setup();
		
		ItemManager.init();
		MonsterItemManager.init();
		getServer().getPluginManager().registerEvents(new ClassEvents(), Instance);
		getServer().getPluginManager().registerEvents(new MonsterEvents(), Instance);
		getServer().getPluginManager().registerEvents(new PlayerEvents(this), Instance);
		getCommand("giveclassmagma").setExecutor(new ClassCommands());
	}
	
	private void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[DvZ]: Plugin is disabled!");
	}
	
	public static DvZ getInstance() {
		return Instance;
	}
	
	private static void setInstance(DvZ instance) {
		DvZ.Instance = instance;
	}
}
