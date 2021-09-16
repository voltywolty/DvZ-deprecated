package main.volt.dvz.events;

import main.volt.dvz.items.MonsterItemManager;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

// FOR MONSTER CLASSES ONLY!

public class MonsterEvents implements Listener {
	// PLAINS WORLD
	static World plainsWorld = Bukkit.getServer().getWorld("dwarf_plains");
	static Location plainsMonsterSpawn = new Location(plainsWorld, 1400, 73, 105); // MONSTER SPAWN
	
	// MOUNTAIN WORLD
	static World mountainWorld = Bukkit.getServer().getWorld("dwarf_mountain");
	static Location mountainMonsterSpawn = new Location(mountainWorld, -188, 81, 337); // MONSTER SPAWN
	
	// DESERT WORLD
	static World desertWorld = Bukkit.getServer().getWorld("dwarf_desert");
	static Location desertMonsterSpawn = new Location(desertWorld, 244, 63, 241); // MONSTER SPAWN
	
	@EventHandler
	private void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		player.setDisplayName(player.getName());
	}
	
	@EventHandler 
	private void onRespawn (PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		player.getInventory().addItem(MonsterItemManager.monsterClassSelector);
	}
	
	@EventHandler
	private void onMonsterClassSelectorRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.monsterClassSelector.getItemMeta())) {
					Player player = event.getPlayer();
					
					Random rand = new Random();
					int monsterChance = rand.nextInt(3);
					
					player.getInventory().addItem(MonsterItemManager.zombieClass); // Guaranteed no matter what
					
					if (monsterChance == 2) {
						player.getInventory().addItem(MonsterItemManager.spiderClass);
					}
					
					player.getInventory().removeItem(MonsterItemManager.monsterClassSelector);
					player.sendMessage(ChatColor.BLUE + "You have been given monsters classes. Right click a disc to become that monster.");
					
					player.setFallDistance(-1);
					player.setCanPickupItems(false);
					
					player.setDisplayName(player.getName() + " §rthe Monster");
				}
			}
		}
	}
	
	@EventHandler
	private void onSuicidePillRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.suicidePill.getItemMeta())) {
					Player player = event.getPlayer();
					player.setHealth(0);
				}
			}
		}
	}
	
	@EventHandler
	private void onZombieClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.zombieClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToPlayers(player, new MobDisguise(DisguiseType.ZOMBIE, true), player);
					
					// Zombie starting gear
					player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
					player.getInventory().addItem(new ItemStack(Material.SPLASH_POTION, 2));
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
					
					player.getInventory().remove(MonsterItemManager.zombieClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					player.teleport(plainsMonsterSpawn);
					// -------------------------------------------
				}
			}
		}
	}
}
