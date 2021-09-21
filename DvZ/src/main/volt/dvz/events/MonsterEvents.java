package main.volt.dvz.events;

import main.volt.dvz.commands.StartCommand;
import main.volt.dvz.items.MonsterItemManager;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
//import net.milkbowl.vault.permission.Permission;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
	static Location plainsMonsterSpawn = new Location(plainsWorld, -376, 87, -227); // MONSTER SPAWN
	
	// MOUNTAIN WORLD
	static World mountainWorld = Bukkit.getServer().getWorld("dwarf_mountain");
	static Location mountainMonsterSpawn = new Location(mountainWorld, -188, 81, 337); // MONSTER SPAWN
	
	// DESERT WORLD
	static World desertWorld = Bukkit.getServer().getWorld("dwarf_desert");
	static Location desertMonsterSpawn = new Location(desertWorld, -228, 63, 165); // MONSTER SPAWN
	
	// RUINS WORLD
	static World ruinsWorld = Bukkit.getServer().getWorld("dwarf_ruins");
	static Location ruinsMonsterSpawn = new Location(ruinsWorld, -378, 86, 177); // MONSTER SPAWN
	
	@EventHandler
	private void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		player.setDisplayName(player.getName());
		
		event.getDrops().clear();
	}
	
	@EventHandler 
	private void onRespawn (PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		DisguiseAPI.undisguiseToAll(player);
		
		if (StartCommand.gameStarted) {
			player.getInventory().addItem(MonsterItemManager.monsterClassSelector);
		}
		
		if (!StartCommand.gameStarted) {
			DisguiseAPI.undisguiseToAll(player);
		}
	}
	
	@EventHandler
	private void onMonsterClassSelectorRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.monsterClassSelector.getItemMeta())) {
					Player player = event.getPlayer();
					
					Random rand = new Random();
					int monsterChance = rand.nextInt(10);
					
					int classChance = rand.nextInt(4);
					
					player.getInventory().addItem(MonsterItemManager.zombieClass); // Guaranteed no matter what
					
					if (monsterChance == 2) {
						player.getInventory().addItem(MonsterItemManager.spiderClass);
						
						if (classChance == 2) {
							player.getInventory().addItem(MonsterItemManager.creeperClass);
						}
					}
					else if (monsterChance == 3) {
						player.getInventory().addItem(MonsterItemManager.creeperClass);
					}
					else if (monsterChance == 4) {
						player.getInventory().addItem(MonsterItemManager.skeletonClass);
					}
					else if (monsterChance == 5) {
						player.getInventory().addItem(MonsterItemManager.wolfClass);
					}
					
					player.getInventory().removeItem(MonsterItemManager.monsterClassSelector);
					player.sendMessage(ChatColor.BLUE + "You have been given monsters classes. Left click a disc to become that monster.");
					player.setCanPickupItems(false);
					
					player.setDisplayName(player.getName() + ChatColor.DARK_RED + " the Monster" + ChatColor.WHITE);
				}
			}
		}
	}
	
	@EventHandler
	private void onSuicidePillRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
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
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.zombieClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.ZOMBIE, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack harmingPotion = new ItemStack(Material.SPLASH_POTION, 2);
					PotionMeta harmingMeta = (PotionMeta)harmingPotion.getItemMeta();
					harmingMeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
					harmingPotion.setItemMeta(harmingMeta);
					
					ItemStack zombieSword = new ItemStack(Material.IRON_SWORD, 1);
					zombieSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
					zombieSword.addEnchantment(Enchantment.DURABILITY, 2);
					
					// ZOMBIE STARTING GEAR
					player.getInventory().addItem(zombieSword);
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
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsMonsterSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainMonsterSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertMonsterSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsMonsterSpawn);
					}
					// -------------------------------------------
				}
			}
		}
	}
	
	// SPIDER CLASS
	@EventHandler
	private void onSpiderClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.spiderClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.CAVE_SPIDER, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack poisonPotion = new ItemStack(Material.SPLASH_POTION, 3);
					PotionMeta poisonMeta = (PotionMeta)poisonPotion.getItemMeta();
					poisonMeta.setBasePotionData(new PotionData(PotionType.POISON, false, false));
					poisonPotion.setItemMeta(poisonMeta);
					
					// SPIDER STARTING GEAR
					player.getInventory().addItem(new ItemStack(Material.SPIDER_EYE, 1));
					player.getInventory().addItem(poisonPotion);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 29400, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 29400, 4));
					
					player.getInventory().remove(MonsterItemManager.spiderClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsMonsterSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainMonsterSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertMonsterSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsMonsterSpawn);
					}
					// -------------------------------------------
				}
			}
		}
	}
	
	@EventHandler
	private void onSpiderEyeHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player attacker = (Player) event.getDamager();
			Player damaged = (Player) event.getEntity();
			
			if (attacker.getInventory().getItemInMainHand().getType() == Material.SPIDER_EYE) {
				Random rand = new Random();
				int randomEffectCounter = rand.nextInt(3);
				
				if (randomEffectCounter == 0) {
					PotionEffect poisonEffect = new PotionEffect(PotionEffectType.POISON, 100, 1);
					poisonEffect.apply(damaged);
				}
				else if (randomEffectCounter == 1) {
					PotionEffect blindnessEffect = new PotionEffect(PotionEffectType.BLINDNESS, 100, 5);
					blindnessEffect.apply(damaged);
				}
				else if (randomEffectCounter == 2) {
					PotionEffect confusionEffect = new PotionEffect(PotionEffectType.CONFUSION, 100, 1);
					confusionEffect.apply(damaged);
				}
			}
		}
	}
	
	// CREEPER CLASS
	@EventHandler
	private void onCreeperClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.creeperClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.CREEPER, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					// CREEPER STARTING GEAR
					player.getInventory().addItem(MonsterItemManager.gunpowderItem);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
					
					player.getInventory().remove(MonsterItemManager.creeperClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsMonsterSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainMonsterSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertMonsterSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsMonsterSpawn);
					}
					// -------------------------------------------
				}
			}
		}
	}
	
	@EventHandler
	private void onGunpowderRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.gunpowderItem.getItemMeta())) {
					Player player = event.getPlayer();
					World world = player.getWorld();
					Location loc = event.getPlayer().getLocation();
					world.createExplosion(loc, 4F, false);
				}
			}
		}
	}
	
	// SKELETON CLASS
	@EventHandler
	private void onSkeletonClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.skeletonClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.SKELETON, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack skeletonBow = new ItemStack(Material.BOW, 1);
					skeletonBow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
					skeletonBow.addEnchantment(Enchantment.DURABILITY, 2);
					skeletonBow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
					
					// SKELETON STARTING GEAR
					player.getInventory().addItem(skeletonBow);
					player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
					player.getInventory().addItem(new ItemStack(Material.VINE, 24));
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
					
					player.getInventory().remove(MonsterItemManager.skeletonClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsMonsterSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainMonsterSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertMonsterSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsMonsterSpawn);
					}
					// -------------------------------------------
				}
			}
		}
	}
	
	// WOLF CLASS
	@EventHandler
	private void onWolfClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.wolfClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.WOLF, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack ironSword = new ItemStack(Material.IRON_SWORD, 1);
					ironSword.addEnchantment(Enchantment.DAMAGE_ALL, 4);
					
					ItemStack goldSword = new ItemStack(Material.GOLDEN_SWORD);
					goldSword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
					goldSword.addEnchantment(Enchantment.KNOCKBACK, 2);
					
					// WOLF STARTING GEAR
					player.getInventory().addItem(ironSword);
					player.getInventory().addItem(goldSword);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(new ItemStack(Material.WOLF_SPAWN_EGG, 5));
					player.getInventory().addItem(new ItemStack(Material.BONE, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
					
					player.getInventory().remove(MonsterItemManager.skeletonClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsMonsterSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainMonsterSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertMonsterSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsMonsterSpawn);
					}
					// -------------------------------------------
				}
			}
		}
	}
	
	// DRAGON ONLY
	@EventHandler
	private void onDragonFireballRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
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
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(MonsterItemManager.lightningStick.getItemMeta())) {
					Player player = event.getPlayer();
					player.getWorld().strikeLightning(player.getTargetBlock(null, 50).getLocation());
				}
			}
		}
	}
}
