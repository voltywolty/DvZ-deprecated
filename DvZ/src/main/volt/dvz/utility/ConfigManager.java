package main.volt.dvz.utility;

import main.volt.dvz.DvZ;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	private DvZ plugin = DvZ.getPlugin(DvZ.class);
	
	// ---------------------
	// Files & Configurations
	public FileConfiguration blacksmithConfig;
	public File blacksmithFile;
	
	public FileConfiguration builderConfig;
	public File builderFile;
	
	public FileConfiguration tailorConfig;
	public File tailorFile;
	// ---------------------
	
	public void setup() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		setupBlacksmithConfig();
		setupBuilderConfig();
		setupTailorConfig();
	}
	
	private void setupBlacksmithConfig() {

		blacksmithFile = new File(plugin.getDataFolder(), "blacksmith.yml");
		
		if (!blacksmithFile.exists()) {
			try {
				blacksmithFile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not load blacksmith.yml file!");
			}
		}
		
		blacksmithConfig = YamlConfiguration.loadConfiguration(blacksmithFile);
	}
	
	private void setupBuilderConfig() {
		builderFile = new File(plugin.getDataFolder(), "builder.yml");
		
		if (!builderFile.exists()) {
			try {
				builderFile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not load builder.yml file!");
			}
		}
		
		builderConfig = YamlConfiguration.loadConfiguration(builderFile);
	}
	
	private void setupTailorConfig() {
		tailorFile = new File(plugin.getDataFolder(), "tailor.yml");
		
		if (!tailorFile.exists()) {
			try {
				tailorFile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not load tailor.yml file!");
			}
		}
		
		tailorConfig = YamlConfiguration.loadConfiguration(tailorFile);
	}
}
