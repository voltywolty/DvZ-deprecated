package main.volt.dvz.events;

import main.volt.dvz.commands.StartCommand;
import main.volt.dvz.items.MonsterItemManager;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;

// FOR MONSTER CLASSES ONLY!
public class MonsterEvents implements Listener {
	// PLAINS WORLD
	static World plainsWorld = Bukkit.getServer().getWorld("dwarf_plains");
	static Location plainsMonsterSpawn = new Location(plainsWorld, -378, 87, -227); // MONSTER SPAWN
	
	// MOUNTAIN WORLD
	static World mountainWorld = Bukkit.getServer().getWorld("dwarf_mountain");
	static Location mountainMonsterSpawn = new Location(mountainWorld, -188, 81, 337); // MONSTER SPAWN
	
	// DESERT WORLD
	static World desertWorld = Bukkit.getServer().getWorld("dwarf_desert");
	static Location desertMonsterSpawn = new Location(desertWorld, -228, 63, 165); // MONSTER SPAWN
	
	// RUINS WORLD
	static World ruinsWorld = Bukkit.getServer().getWorld("dwarf_ruins");
	static Location ruinsMonsterSpawn = new Location(ruinsWorld, -378, 86, 177); // MONSTER SPAWN
	
	int totalUses = 8;
	boolean canUse = true;
	
	public Set<UUID> isMonster = new HashSet<UUID>();
	public static boolean canSpawn = false;
	
	// --------------------
	// MONSTER ARMOR
	private ItemStack ironHelmet = new ItemStack(Material.IRON_HELMET, 1);
	private ItemStack ironChestplate = new ItemStack(Material.IRON_CHESTPLATE, 1);
	private ItemStack ironLeggings = new ItemStack(Material.IRON_LEGGINGS, 1);
	private ItemStack ironBoots = new ItemStack(Material.IRON_BOOTS, 1);
	
	private ItemStack leatherHelmet = new ItemStack(Material.LEATHER_HELMET, 1);
	private ItemStack leatherChestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
	private ItemStack leatherLeggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
	private ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS, 1);
	// --------------------
	
	@EventHandler
	private void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		player.setDisplayName(player.getName());
		
		if (isMonster.contains(player.getUniqueId()) && StartCommand.gameStarted) {
			event.getDrops().clear();
		}
	}
	
	@EventHandler 
	private void onRespawn (PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		DisguiseAPI.undisguiseToAll(player);
		
		isMonster.remove(player.getUniqueId());
		
		if (StartCommand.gameStarted && canSpawn) {
			player.getInventory().addItem(MonsterItemManager.monsterClassSelector);
		}
		
		if (!StartCommand.gameStarted) {
			DisguiseAPI.undisguiseToAll(player);
			isMonster.remove(player.getUniqueId());
		}
		
		totalUses = 8;
		canUse = true;
	}
	
	@EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
		Player player = (Player) event.getEntity();
		
		if (isMonster.contains(player.getUniqueId())) {
			if (event.getTarget() instanceof Player) {
	            if (event.getEntity() instanceof Monster) {
	                event.setCancelled(true);
	                return;
	            }
	        }
		}
		else if (!(event.getTarget() instanceof Player)) {
			return;
		}
    }
	
	@EventHandler
	private void onMonsterClassSelectorRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.monsterClassSelector.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					ThreadLocalRandom rand = ThreadLocalRandom.current();
					int monsterChance = rand.nextInt(20);
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
						
						if (classChance == 3) {
							player.getInventory().addItem(MonsterItemManager.wolfClass);
						}
					}
					else if (monsterChance == 4) {
						player.getInventory().addItem(MonsterItemManager.skeletonClass);
					}
					else if (monsterChance == 6) {
						player.getInventory().addItem(MonsterItemManager.broodmotherClass);
						
						if (classChance == 0) {
							player.getInventory().addItem(MonsterItemManager.creeperClass);
						}
					}
					else if (monsterChance == 7) {
						player.getInventory().addItem(MonsterItemManager.wolfClass);
						
						if (classChance == 1) {
							player.getInventory().addItem(MonsterItemManager.chickenNuggetClass);
						}
					}
					else if (monsterChance == 12) {
						player.getInventory().addItem(MonsterItemManager.cougarClass);
						
						if (classChance == 1) {
							player.getInventory().addItem(MonsterItemManager.skeletonClass);
						}
						else if (classChance == 2) {
							player.getInventory().addItem(MonsterItemManager.hungryPigClass);
						}
					}
					else if (monsterChance == 16) {
						player.getInventory().addItem(MonsterItemManager.hungryPigClass);
					}
					else if (monsterChance == 17) {
						player.getInventory().addItem(MonsterItemManager.chickenNuggetClass);
						
						if (classChance == 0) {
							player.getInventory().addItem(MonsterItemManager.wolfClass);
							player.getInventory().addItem(MonsterItemManager.spiderClass);
						}
					}
					
					player.getInventory().removeItem(MonsterItemManager.monsterClassSelector);
					player.sendMessage(ChatColor.DARK_AQUA + "You have been given monsters classes. Left click a disc to become that monster.");
					player.setCanPickupItems(false);
					
					player.setDisplayName(player.getName() + ChatColor.DARK_RED + " the Monster" + ChatColor.WHITE);
					isMonster.add(player.getUniqueId());
				}
			}
		}
	}
	
	@EventHandler
	private void onSuicidePillRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.suicidePill.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					player.setHealth(0);
				}
			}
		}
	}
	
	@EventHandler
	private void onZombieClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.zombieClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
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
					zombieSword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
					zombieSword.addEnchantment(Enchantment.DURABILITY, 2);
					
					ironHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					
					// ZOMBIE STARTING GEAR
					player.getInventory().addItem(zombieSword);
					player.getInventory().addItem(harmingPotion);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(ironHelmet);
					player.getInventory().setChestplate(ironChestplate);
					player.getInventory().setLeggings(ironLeggings);
					player.getInventory().setBoots(ironBoots);
					
					player.getInventory().remove(MonsterItemManager.zombieClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
		if (event.getItem().getItemMeta().equals(MonsterItemManager.spiderClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.CAVE_SPIDER, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack poisonPotion = new ItemStack(Material.SPLASH_POTION, 3);
					PotionMeta poisonMeta = (PotionMeta)poisonPotion.getItemMeta();
					poisonMeta.setBasePotionData(new PotionData(PotionType.POISON, false, false));
					poisonPotion.setItemMeta(poisonMeta);
					
					ironHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					
					// SPIDER STARTING GEAR
					player.getInventory().addItem(new ItemStack(Material.SPIDER_EYE, 1));
					player.getInventory().addItem(poisonPotion);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(ironHelmet);
					player.getInventory().setChestplate(ironChestplate);
					player.getInventory().setLeggings(ironLeggings);
					player.getInventory().setBoots(ironBoots);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 29400, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 29400, 4));
					
					player.getInventory().remove(MonsterItemManager.spiderClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
				ThreadLocalRandom rand = ThreadLocalRandom.current();
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
		if (event.getItem().getItemMeta().equals(MonsterItemManager.creeperClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.CREEPER, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);

					// CREEPER STARTING GEAR
					player.getInventory().addItem(MonsterItemManager.gunpowderItem);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(leatherHelmet);
					player.getInventory().setChestplate(leatherChestplate);
					player.getInventory().setLeggings(leatherLeggings);
					player.getInventory().setBoots(leatherBoots);
					
					player.getInventory().remove(MonsterItemManager.creeperClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
		if (event.getItem().getItemMeta().equals(MonsterItemManager.gunpowderItem.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					World world = player.getWorld();
					Location loc = event.getPlayer().getLocation();
					world.createExplosion(loc, 5F, false);
				}
			}
		}
	}
	
	// SKELETON CLASS
	@EventHandler
	private void onSkeletonClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.skeletonClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.SKELETON, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					ItemStack skeletonBow = new ItemStack(Material.BOW, 1);
					skeletonBow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
					skeletonBow.addEnchantment(Enchantment.DURABILITY, 2);
					skeletonBow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
					
					leatherHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					
					// SKELETON STARTING GEAR
					player.getInventory().addItem(skeletonBow);
					player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
					player.getInventory().addItem(new ItemStack(Material.VINE, 24));
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(leatherHelmet);
					player.getInventory().setChestplate(leatherChestplate);
					player.getInventory().setLeggings(leatherLeggings);
					player.getInventory().setBoots(leatherBoots);
					
					player.getInventory().remove(MonsterItemManager.skeletonClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
		if (event.getItem().getItemMeta().equals(MonsterItemManager.wolfClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
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
					
					ironHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					ironBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					
					// WOLF STARTING GEAR
					player.getInventory().addItem(ironSword);
					player.getInventory().addItem(goldSword);
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(new ItemStack(Material.WOLF_SPAWN_EGG, 5));
					player.getInventory().addItem(new ItemStack(Material.BONE, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(ironHelmet);
					player.getInventory().setChestplate(ironChestplate);
					player.getInventory().setLeggings(ironLeggings);
					player.getInventory().setBoots(ironBoots);
					
					player.getInventory().remove(MonsterItemManager.skeletonClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
	
	// BROODMOTHER CLASS
	@EventHandler
	private void onBroodmotherClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.broodmotherClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.SILVERFISH, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					leatherHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					
					// BROODMOTHER STARTING GEAR
					player.getInventory().addItem(new ItemStack(Material.COOKED_COD, 1));
					player.getInventory().addItem(new ItemStack(Material.COD, 1));
					player.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL, 1));
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(new ItemStack(Material.SILVERFISH_SPAWN_EGG, 5));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(leatherHelmet);
					player.getInventory().setChestplate(leatherChestplate);
					player.getInventory().setLeggings(leatherLeggings);
					player.getInventory().setBoots(leatherBoots);
					
					player.getInventory().remove(MonsterItemManager.broodmotherClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
	private void onCookedCodUse(PlayerInteractEvent event) {
		Block blockToBreak = event.getClickedBlock();
		Player player = event.getPlayer();
		
		if (player.getInventory().getItemInMainHand().getType() == Material.COOKED_COD) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					if (blockToBreak.getType().equals(Material.INFESTED_STONE)) {
						blockToBreak.breakNaturally();
						blockToBreak.getRelative(BlockFace.NORTH).breakNaturally();
						blockToBreak.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).breakNaturally();
						blockToBreak.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).breakNaturally();
						blockToBreak.getRelative(BlockFace.SOUTH).breakNaturally();
						blockToBreak.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).breakNaturally();
						blockToBreak.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).breakNaturally();
						blockToBreak.getRelative(BlockFace.EAST).breakNaturally();
						blockToBreak.getRelative(BlockFace.WEST).breakNaturally();
					} 
				}
			}
		}
	}
	
	@EventHandler
	private void onCodUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block stoneBlocks = event.getClickedBlock();
		
		if (player.getInventory().getItemInMainHand().getType() == Material.COD) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					if (stoneBlocks.getType().equals(Material.STONE_BRICKS) || stoneBlocks.getType().equals(Material.MOSSY_STONE_BRICKS) || stoneBlocks.getType().equals(Material.CRACKED_STONE_BRICKS) || stoneBlocks.getType().equals(Material.CHISELED_STONE_BRICKS)) {
						totalUses--;
						
						if (totalUses <= 0) {
							player.sendMessage(ChatColor.RED + "You can no longer use this ability.");
							canUse = false;
						}
						else if (canUse) {
							stoneBlocks.setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.NORTH).setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.SOUTH).setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.EAST).setType(Material.INFESTED_STONE);
							stoneBlocks.getRelative(BlockFace.WEST).setType(Material.INFESTED_STONE);
						}
					}
				}
			}
		}
	}
	
	// CHICKEN NUGGET CLASS
	@EventHandler
	private void onChickenNuggetClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.chickenNuggetClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.CHICKEN, true));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					// CHICKEN NUGGET STARTING GEAR
					player.getInventory().addItem(new ItemStack(Material.EGG, 10));
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
					
					player.getInventory().remove(MonsterItemManager.chickenNuggetClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
	private void onEggHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Egg && event.getEntity().getShooter() instanceof Player) {
			Entity egg = event.getEntity();
			
			Location location = egg.getLocation();
			World world = event.getEntity().getWorld();
			world.createExplosion(location, 2F);
		}
	}
	
	// HUNGRY PIG CLASS
	@EventHandler
	private void onHungryPigClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.hungryPigClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.PIG, false));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					leatherHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					
					// HUNGRY PIG STARTING GEAR
					player.getInventory().addItem(new ItemStack(Material.NETHER_WART, 1));
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(leatherHelmet);
					player.getInventory().setChestplate(leatherChestplate);
					player.getInventory().setLeggings(leatherLeggings);
					player.getInventory().setBoots(leatherBoots);
					
					player.getInventory().remove(MonsterItemManager.hungryPigClass);
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
	private void onNetherWartUse(PlayerInteractEvent event) {
		Block blockToBreak = event.getClickedBlock();
		Player player = event.getPlayer();
		
		if (player.getInventory().getItemInMainHand().getType() == Material.NETHER_WART) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					if (blockToBreak.getType().equals(Material.STONE_BRICKS) || blockToBreak.getType().equals(Material.MOSSY_STONE_BRICKS) || blockToBreak.getType().equals(Material.CRACKED_STONE_BRICKS) || blockToBreak.getType().equals(Material.CHISELED_STONE_BRICKS)) {
						blockToBreak.breakNaturally();
						
						player.getWorld().spawnParticle(Particle.ASH, player.getEyeLocation(), 5);
						player.giveExp(1);
					} 
				}
			}
			else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getTotalExperience() >= 91 || event.getAction() == Action.RIGHT_CLICK_AIR && player.getTotalExperience() >= 91) {
				player.setExp(0);
				DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.PIG, true));
				
				player.getInventory().clear();
				
				ItemStack stoneSword = new ItemStack(Material.STONE_SWORD, 1);
				stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
				
				leatherHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				leatherChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				leatherLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				leatherBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				
				// HUNGRY PIG UPGRADE 2
				player.getInventory().addItem(new ItemStack(Material.FERMENTED_SPIDER_EYE, 1));
				player.getInventory().addItem(stoneSword);
				player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
				player.getInventory().addItem(MonsterItemManager.suicidePill);
				
				player.getInventory().setHelmet(leatherHelmet);
				player.getInventory().setChestplate(leatherChestplate);
				player.getInventory().setLeggings(leatherLeggings);
				player.getInventory().setBoots(leatherBoots);
			}
		}
	}
	
	@EventHandler
	private void onFermentedSpiderEyeUse(PlayerInteractEvent event) {
		Block blockToBreak = event.getClickedBlock();
		Player player = event.getPlayer();
		
		if (player.getInventory().getItemInMainHand().getType() == Material.FERMENTED_SPIDER_EYE) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					if (blockToBreak.getType().equals(Material.STONE_BRICKS) || blockToBreak.getType().equals(Material.MOSSY_STONE_BRICKS) || blockToBreak.getType().equals(Material.CRACKED_STONE_BRICKS) || blockToBreak.getType().equals(Material.CHISELED_STONE_BRICKS)) {
						blockToBreak.breakNaturally();
						player.giveExp(1);
					} 
				}
			}
			else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getTotalExperience() >= 160 || event.getAction() == Action.RIGHT_CLICK_AIR && player.getTotalExperience() >= 160) {
				player.setExp(0);
				//DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.PIG_ZOMBIE)); -- Fix later on.
				
				player.getInventory().clear();
				
				ItemStack stoneSword = new ItemStack(Material.STONE_SWORD, 1);
				stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
				
				ironHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				ironChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				ironLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				ironBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
				
				// HUNGRY PIG UPGRADE 3
				player.getInventory().addItem(stoneSword);
				player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
				player.getInventory().addItem(MonsterItemManager.suicidePill);
				
				player.getInventory().setHelmet(ironHelmet);
				player.getInventory().setChestplate(ironChestplate);
				player.getInventory().setLeggings(ironLeggings);
				player.getInventory().setBoots(ironBoots);
			}
		}
	}
	
	// COUGAR CLASS
	@EventHandler
	private void onCougarClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.cougarClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.OCELOT, false));
					DisguiseAPI.setViewDisguiseToggled(player, false);
					DisguiseAPI.setActionBarShown(player, false);
					
					leatherHelmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherChestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherLeggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					leatherBoots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 6);
					
					// COUGAR STARTING GEAR
					player.getInventory().addItem(new ItemStack(Material.INK_SAC, 1));
					player.getInventory().addItem(new ItemStack(Material.STRING, 1));
					player.getInventory().addItem(new ItemStack(Material.VINE, 24));
					player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
					player.getInventory().addItem(MonsterItemManager.suicidePill);
					
					player.getInventory().setHelmet(leatherHelmet);
					player.getInventory().setChestplate(leatherChestplate);
					player.getInventory().setLeggings(leatherLeggings);
					player.getInventory().setBoots(leatherBoots);
					
					player.getInventory().remove(MonsterItemManager.cougarClass);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 29400, 0));
					
					plainsMonsterSpawn.setWorld(plainsWorld);
					mountainMonsterSpawn.setWorld(mountainWorld);
					desertMonsterSpawn.setWorld(desertWorld);
					ruinsMonsterSpawn.setWorld(ruinsWorld);
					
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
	private void onInkSacHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player attacker = (Player) event.getDamager();
			Player damaged = (Player) event.getEntity();
			
			if (attacker.getInventory().getItemInMainHand().getType() == Material.INK_SAC) {
				PotionEffect hungerDrain = new PotionEffect(PotionEffectType.HUNGER, 100, 3);
				hungerDrain.apply(damaged);
			}
		}
	}
	
	HashMap<String, Long> spellCooldown = new HashMap<String, Long>();
	
	@EventHandler
	private void onStringUse(EntityDamageByEntityEvent event) {		
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player attacker = (Player) event.getDamager();
			Player damaged = (Player) event.getEntity();
			
			if (attacker.getInventory().getItemInMainHand().getType() == Material.STRING) {
				if (spellCooldown.containsKey(attacker.getName())) {
					if (spellCooldown.get(attacker.getName()) > System.currentTimeMillis()) {
						long secondsLeft = (spellCooldown.get(attacker.getName()) - System.currentTimeMillis()) / 1000;
						attacker.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
						return;
					}
				}
				
				spellCooldown.put(attacker.getName(), System.currentTimeMillis() + (5 * 1000));
				
				ItemStack item = damaged.getInventory().getItemInMainHand();
				damaged.getInventory().remove(item);
				
				Item itemDropped = damaged.getWorld().dropItemNaturally(damaged.getLocation(), item);
				itemDropped.setPickupDelay(30);
			}
		}
	}
	
	// --------------------------------
	// DRAGONS
	// --------------------------------
	
	// VLAURUNGA
	@EventHandler
	private void onDragonFireballRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.dragonFireball.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					player.launchProjectile(Fireball.class).setVelocity(player.getLocation().getDirection().multiply(0.7));
				}
			}
		}
	}
	
	// AVIRELLA
	@EventHandler
	private void onLightningStickRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.lightningStick.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					player.getWorld().strikeLightning(player.getTargetBlock(null, 50).getLocation());
				}
			}
		}
	}
	
	@EventHandler
	private void onDwarfLauncherLeftClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(MonsterItemManager.dwarfLauncher.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
						entity.setVelocity(new Vector(0.0, 5, 0.0));
					}
				}
			}
		}
	}
}
