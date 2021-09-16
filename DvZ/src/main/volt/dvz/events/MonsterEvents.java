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
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

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
					
					player.setDisplayName(player.getName() + " �rthe Monster");
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
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack harmingPotion = new ItemStack(Material.SPLASH_POTION, 2);
					PotionMeta harmingMeta = (PotionMeta)harmingPotion.getItemMeta();
					harmingMeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
					harmingPotion.setItemMeta(harmingMeta);
					
					// ZOMBIE STARTING GEAR
					player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
					player.getInventory().addItem(harmingPotion);
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
	
	@EventHandler
	private void onSpiderClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.spiderClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToPlayers(player, new MobDisguise(DisguiseType.SPIDER, true), player);
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack poisonPotion = new ItemStack(Material.SPLASH_POTION, 3);
					PotionMeta poisonMeta = (PotionMeta)poisonPotion.getItemMeta();
					poisonMeta.setBasePotionData(new PotionData(PotionType.POISON, false, false));
					poisonPotion.setItemMeta(poisonMeta);
					
					// SPIDER STARTING GEAR
					// add custom spider eye
					player.getInventory().addItem(poisonPotion);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1470, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1470, 4));
					
					player.getInventory().remove(MonsterItemManager.spiderClass);
					
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
	
	// DRAGON ONLY
	@EventHandler
	private void onDragonFireballRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.dragonFireball.getItemMeta())) {
					Player player = event.getPlayer();
					player.launchProjectile(Fireball.class).setVelocity(player.getLocation().getDirection().multiply(0.7));
				}
			}
		}
	}
	
	@EventHandler
	private void onLightningStickRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.lightningStick.getItemMeta())) {
					Player player = event.getPlayer();
					player.getWorld().strikeLightning(player.getTargetBlock(null, 50).getLocation());
				}
			}
		}
	}
}
