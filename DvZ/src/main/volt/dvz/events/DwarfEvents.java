package main.volt.dvz.events;

import main.volt.dvz.DvZ;
import main.volt.dvz.items.ItemManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

// THIS CLASS IS FOR DWARVES ONLY! MONSTERS ARE TO BE HANDLED WITH IN MonsterEvents
public class DwarfEvents implements Listener {
	
	// PLAINS WORLD
	static World plainsWorld = Bukkit.getServer().getWorld("dwarf_plains");
	static Location plainsSpawn = new Location(plainsWorld, -699, 77, -223); // DWARF SPAWN
	
	// MOUNTAIN WORLD
	static World mountainWorld = Bukkit.getServer().getWorld("dwarf_mountain");
	static Location mountainSpawn = new Location(mountainWorld, -152, 116, 498); // DWARF SPAWN
	
	// DESERT WORLD
	static World desertWorld = Bukkit.getServer().getWorld("dwarf_desert");
	static Location desertSpawn = new Location(desertWorld, -38, 70, 240); // DWARF SPAWN
	
	// RUINS WORLD
	static World ruinsWorld = Bukkit.getServer().getWorld("dwarf_ruins");
	static Location ruinsSpawn = new Location(ruinsWorld, -253, 77, 149); // DWARF SPAWN
	
	public Set<UUID> isDragonWarrior = new HashSet<UUID>();
	
	// THIS IS FOR THE CLASS GIVER (MAGMA CREAM)
	@EventHandler
	public void onClassGiverRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getItem().getItemMeta().equals(ItemManager.classSelector.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					ThreadLocalRandom rand = ThreadLocalRandom.current();
					int classChance = rand.nextInt(9);
					int classChance2 = rand.nextInt(4);
					
					player.getInventory().addItem(ItemManager.dwarfBuilderClass); // This is guaranteed no matter what
					
					if (classChance == 2) {
						player.getInventory().addItem(ItemManager.dwarfBlacksmithClass);
						
						if (classChance2 == 2) {
							player.getInventory().addItem(ItemManager.dwarfAlchemistClass);
						}
						
						if (classChance2 == 3) {
							player.getInventory().addItem(ItemManager.dwarfTailorClass);
						}
					}
					else if (classChance == 3) {
						player.getInventory().addItem(ItemManager.dwarfTailorClass);
						
						if (classChance2 == 1) {
							player.getInventory().addItem(ItemManager.dwarfBakerClass);
						}
					}
					else if (classChance == 4) {
						player.getInventory().addItem(ItemManager.dwarfBakerClass);
						
						if (classChance2 == 2) {
							player.getInventory().addItem(ItemManager.dwarfTailorClass);
						}
					}
					else if (classChance == 5) {
						player.getInventory().addItem(ItemManager.dwarfAlchemistClass);
					}
					
					player.getInventory().removeItem(ItemManager.classSelector);
					player.sendMessage(ChatColor.DARK_AQUA + "You have been given classes. Left click a disc to become that class.");
					
					player.setFallDistance(3);
					player.setCanPickupItems(true);
				}
			}
		}
	}
	
	// BUILDER CLASS
	@EventHandler
	public static void onBuilderClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfBuilderClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					// BUILDER STARTER ITEMS & ARMOR
					player.getInventory().addItem(ItemManager.dwarfBuilderBook);
					player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
					player.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
					player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
					player.getInventory().addItem(new ItemStack(Material.COOKED_PORKCHOP, 16));
					
					player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
					player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
					player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					ruinsSpawn.setWorld(ruinsWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsSpawn);
					}
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfBuilderClass);
					
					player.sendMessage(ChatColor.DARK_AQUA + "You have become a Builder Dwarf.");
					player.setDisplayName(player.getName() + ChatColor.DARK_AQUA + " the Builder" + ChatColor.WHITE);
				}
			}
		}
	}
	
	HashMap<String, Long> bookCooldown = new HashMap<String, Long>();
	
	@EventHandler
	public void onBuilderBookRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfBuilderBook.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					ThreadLocalRandom rand = ThreadLocalRandom.current();
					int chance = rand.nextInt(5);
					
					if (bookCooldown.containsKey(player.getName())) {
						if (bookCooldown.get(player.getName()) > System.currentTimeMillis()) {
							long secondsLeft = (bookCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
							player.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
							return;
						}
					}
					
					bookCooldown.put(player.getName(), System.currentTimeMillis() + (30 * 1000));
					
					List <String> buildItems = DvZ.getInstance().configManager.builderConfig.getStringList("BuilderBlocks");
					int index = ThreadLocalRandom.current().nextInt(buildItems.size());
					String items = buildItems.get(index);
					
					ItemStack newItem = new ItemStack(Material.getMaterial(items.toUpperCase()), 64);
					
					player.getWorld().dropItem(player.getEyeLocation(), newItem);
					
					if (chance == 3) {
						player.getWorld().dropItem(player.getEyeLocation(), newItem);
					}
					else if (chance == 4) {
						player.getWorld().dropItem(player.getEyeLocation(), newItem);
						player.getWorld().dropItem(player.getEyeLocation(), newItem);
					}
					
					player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.GLOWSTONE_DUST, 3));
					player.giveExp(5);
				}
			}
		}
	}
	
	// BLACKSMITH CLASS
	@EventHandler
	public static void onBlacksmithClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfBlacksmithClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					// BLACKSMITH STARTER ITEMS
					player.getInventory().addItem(ItemManager.dwarfBlacksmithBook);
					player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
					player.getInventory().addItem(new ItemStack(Material.REDSTONE_ORE, 8));
					player.getInventory().addItem(new ItemStack(Material.GOLD_ORE, 24));
					player.getInventory().addItem(new ItemStack(Material.FURNACE, 4));
					player.getInventory().addItem(new ItemStack(Material.COAL, 10));
					player.getInventory().addItem(new ItemStack(Material.CHEST, 2));
					player.getInventory().addItem(new ItemStack(Material.OAK_SIGN, 3));
					player.getInventory().addItem(new ItemStack(Material.NETHER_BRICKS, 64));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					ruinsSpawn.setWorld(ruinsWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsSpawn);
					}
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfBlacksmithClass);
					
					player.sendMessage(ChatColor.DARK_AQUA + "You have become a Blacksmith Dwarf.");
					player.setDisplayName(player.getName() + ChatColor.DARK_AQUA + " the Blacksmith" + ChatColor.WHITE);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlacksmithBookRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfBlacksmithBook.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					ThreadLocalRandom rand = ThreadLocalRandom.current();
					int randomTool = rand.nextInt(7);
					
					ItemStack clockCheck = new ItemStack(Material.CLOCK);
					if (player.getInventory().containsAtLeast(clockCheck, 3)) {
						List <String> toolItems = DvZ.getInstance().configManager.blacksmithConfig.getStringList("BlacksmithTools");
						int index = ThreadLocalRandom.current().nextInt(toolItems.size());
						String items = toolItems.get(index);
						
						ItemStack newItem = new ItemStack(Material.getMaterial(items.toUpperCase()));
						
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.COAL, 8));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.ARROW, 20));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.REDSTONE_ORE, 3));
						player.getWorld().dropItem(player.getEyeLocation(), newItem);
						
						if (randomTool == 2) {
							player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.DIAMOND_SHOVEL, 1));
						}
						else if (randomTool == 4) {
							player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.DIAMOND_PICKAXE, 1));
						}
						else if (randomTool == 6) {
							player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.DIAMOND_AXE, 1));
						}
						
						player.getInventory().removeItem(new ItemStack(Material.CLOCK, 3));
						player.giveExp(5);
					}
					else if (!player.getInventory().containsAtLeast(clockCheck, 3)) {
						player.sendMessage(ChatColor.DARK_AQUA + "You do not have three clocks in your inventory! Smelt gold and redstone and craft them into clocks.");
					}
				}
			}
		}
	}
	
	// TAILOR CLASS
	@EventHandler
	public static void onTailorClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfTailorClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					// TAILOR STARTER ITEMS
					player.getInventory().addItem(ItemManager.dwarfTailorBook);
					player.getInventory().addItem(new ItemStack(Material.BONE, 10));
					player.getInventory().addItem(new ItemStack(Material.BIRCH_SAPLING, 6));
					player.getInventory().addItem(new ItemStack(Material.GRASS_BLOCK, 20));
					player.getInventory().addItem(new ItemStack(Material.OAK_SIGN, 3));
					player.getInventory().addItem(new ItemStack(Material.BROWN_MUSHROOM_BLOCK, 64));
					player.getInventory().addItem(new ItemStack(Material.TORCH, 32));
					player.getInventory().addItem(new ItemStack(Material.CHEST, 2));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					ruinsSpawn.setWorld(ruinsWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsSpawn);
					}
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfTailorClass);
					
					player.sendMessage(ChatColor.DARK_AQUA + "You have become a Tailor Dwarf.");
					player.setDisplayName(player.getName() + ChatColor.DARK_AQUA + " the Tailor" + ChatColor.WHITE);
				}
			}
		}
	}
	
	@EventHandler
	public void onTailorBookRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfTailorBook.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					if (bookCooldown.containsKey(player.getName())) {
						if (bookCooldown.get(player.getName()) > System.currentTimeMillis()) {
							long secondsLeft = (bookCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
							player.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
							return;
						}
					}
					
					bookCooldown.put(player.getName(), System.currentTimeMillis() + (10 * 1000));
					
					ItemStack dyeCheck = new ItemStack(Material.ORANGE_DYE);

					if (player.getInventory().containsAtLeast(dyeCheck, 10)) {
						List <String> armorItems = DvZ.getInstance().configManager.tailorConfig.getStringList("TailorArmors");
						int index = ThreadLocalRandom.current().nextInt(armorItems.size());
						String items = armorItems.get(index);
						
						ItemStack newItem = new ItemStack(Material.getMaterial(items.toUpperCase()));
						
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.GOLD_ORE, 10));
						player.getWorld().dropItem(player.getEyeLocation(), newItem);
						
						player.getInventory().removeItem(new ItemStack(Material.ORANGE_DYE, 10));
						player.giveExp(5);
					}
					else if (!player.getInventory().containsAtLeast(dyeCheck, 10)) {
						player.sendMessage(ChatColor.DARK_AQUA + "You do not have 10 orange dye in your inventory! Use bonemeal and craft orange dye.");
					}
				}
			}
		}
	}
	
	// ALCHEMIST CLASS
	@EventHandler
	public static void onAlchemistClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfAlchemistClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					// TAILOR STARTER ITEMS
					player.getInventory().addItem(ItemManager.dwarfAlchemistBook);
					player.getInventory().addItem(new ItemStack(Material.BREWING_STAND, 2));
					player.getInventory().addItem(new ItemStack(Material.CAULDRON, 2));
					player.getInventory().addItem(new ItemStack(Material.CHEST, 2));
					player.getInventory().addItem(new ItemStack(Material.REDSTONE, 5));
					player.getInventory().addItem(new ItemStack(Material.BLAZE_POWDER, 16));
					player.getInventory().addItem(new ItemStack(Material.OAK_SIGN, 3));
					player.getInventory().addItem(new ItemStack(Material.LAPIS_BLOCK, 64));
					player.getInventory().addItem(new ItemStack(Material.GLASS, 64));
					player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
					player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					ruinsSpawn.setWorld(ruinsWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsSpawn);
					}
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfAlchemistClass);
					
					player.sendMessage(ChatColor.DARK_AQUA + "You have become a Alchemist Dwarf.");
					player.setDisplayName(player.getName() + ChatColor.DARK_AQUA + " the Alchemist" + ChatColor.WHITE);
				}
			}
		}
	}
	
	@EventHandler
	public void onAlchemistBookRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfAlchemistBook.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					if (bookCooldown.containsKey(player.getName())) {
						if (bookCooldown.get(player.getName()) > System.currentTimeMillis()) {
							long secondsLeft = (bookCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
							player.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
							return;
						}
					}
					
					bookCooldown.put(player.getName(), System.currentTimeMillis() + (15 * 1000));
					
					ItemStack potionCheck = new ItemStack(Material.POTION);
					PotionMeta potionMeta = (PotionMeta)potionCheck.getItemMeta();
					potionMeta.setBasePotionData(new PotionData(PotionType.MUNDANE));
					potionCheck.setItemMeta(potionMeta);

					if (player.getInventory().containsAtLeast(potionCheck, 4)) {		
						ThreadLocalRandom rand = ThreadLocalRandom.current();
						int chance = rand.nextInt(3);
						int firePotChance = rand.nextInt(20);
						
						ItemStack healthPotion = new ItemStack(Material.POTION, 3);
						PotionMeta healthMeta = (PotionMeta)healthPotion.getItemMeta();
						healthMeta.setBasePotionData(new PotionData(PotionType.REGEN, false, false));
						healthPotion.setItemMeta(healthMeta);
						
						ItemStack speedPotion = new ItemStack(Material.POTION, 3);
						PotionMeta speedMeta = (PotionMeta)speedPotion.getItemMeta();
						speedMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, false));
						speedPotion.setItemMeta(speedMeta);
						
						ItemStack strengthPotion = new ItemStack(Material.POTION, 3);
						PotionMeta strengthMeta = (PotionMeta)strengthPotion.getItemMeta();
						strengthMeta.setBasePotionData(new PotionData(PotionType.STRENGTH, false, false));
						strengthPotion.setItemMeta(strengthMeta);
						
						ItemStack firePotion = new ItemStack(Material.POTION, 3);
						PotionMeta fireMeta = (PotionMeta)firePotion.getItemMeta();
						fireMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE, false, false));
						firePotion.setItemMeta(fireMeta);
						
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.BONE, 3));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.MILK_BUCKET, 1));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.SAND, 3));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.LAPIS_LAZULI, 4));
						
						if (chance == 0) {
							player.getWorld().dropItem(player.getEyeLocation(), healthPotion);
						}
						else if (chance == 1) {
							player.getWorld().dropItem(player.getEyeLocation(), speedPotion);
						}
						else if (chance == 2) {
							player.getWorld().dropItem(player.getEyeLocation(), strengthPotion);
						}
						
						if (firePotChance == 4) {
							player.getWorld().dropItem(player.getEyeLocation(), firePotion );
						}
						
						player.getInventory().removeItem(potionCheck);
						player.getInventory().removeItem(potionCheck);
						player.getInventory().removeItem(potionCheck);
						player.getInventory().removeItem(potionCheck);
						player.giveExp(5);
					}
					else if (!player.getInventory().containsAtLeast(potionCheck, 4)) {
						player.sendMessage(ChatColor.DARK_AQUA + "You do not have 4 mundane potions in your inventory! Fill your brew your bottles of water to get mundane potions.");
					}
				}
			}
		}
	}
	
	HashMap<String, Long> potionCooldown = new HashMap<String, Long>();
	
	@EventHandler
	public void onPotionUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		ItemStack healthPotion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta healthMeta = (PotionMeta)healthPotion.getItemMeta();
		healthMeta.setBasePotionData(new PotionData(PotionType.REGEN, false, false));
		healthPotion.setItemMeta(healthMeta);
		
		ItemStack speedPotion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta speedMeta = (PotionMeta)speedPotion.getItemMeta();
		speedMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, false));
		speedPotion.setItemMeta(speedMeta);
		
		ItemStack strengthPotion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta strengthMeta = (PotionMeta)strengthPotion.getItemMeta();
		strengthMeta.setBasePotionData(new PotionData(PotionType.STRENGTH, false, false));
		strengthPotion.setItemMeta(strengthMeta);
		
		ItemStack firePotion = new ItemStack(Material.SPLASH_POTION);
		PotionMeta fireMeta = (PotionMeta)firePotion.getItemMeta();
		fireMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE, false, false));
		firePotion.setItemMeta(fireMeta);
		
		if (event.getItem().getItemMeta().equals(healthPotion.getItemMeta())) {
			if (potionCooldown.containsKey(player.getName())) {
				if (potionCooldown.get(player.getName()) > System.currentTimeMillis()) {
					long secondsLeft = (potionCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
					return;
				}
			}
			
			potionCooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
			
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					ItemStack healingPotion = new ItemStack(Material.SPLASH_POTION);
					PotionMeta healingMeta = (PotionMeta) healingPotion.getItemMeta();
					healingMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 3600, 0), true);
					healingPotion.setItemMeta(healingMeta);
					
					ThrownPotion thrownHealingPotion = player.launchProjectile(ThrownPotion.class);
					thrownHealingPotion.setItem(healingPotion);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3600, 0));
				}
			}
		}
		else if (event.getItem().getItemMeta().equals(speedPotion.getItemMeta())) {
			if (potionCooldown.containsKey(player.getName())) {
				if (potionCooldown.get(player.getName()) > System.currentTimeMillis()) {
					long secondsLeft = (potionCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
					return;
				}
			}
			
			potionCooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
			
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					ItemStack speedingPotion = new ItemStack(Material.SPLASH_POTION);
					PotionMeta speedingMeta = (PotionMeta) speedingPotion.getItemMeta();
					speedingMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0), true);
					speedingPotion.setItemMeta(speedingMeta);
					
					ThrownPotion thrownSpeedingPotion = player.launchProjectile(ThrownPotion.class);
					thrownSpeedingPotion.setItem(speedingPotion);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0));
				}
			}
		}
		else if (event.getItem().getItemMeta().equals(strengthPotion.getItemMeta())) {
			if (potionCooldown.containsKey(player.getName())) {
				if (potionCooldown.get(player.getName()) > System.currentTimeMillis()) {
					long secondsLeft = (potionCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
					return;
				}
			}
			
			potionCooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
			
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					ItemStack strengthingPotion = new ItemStack(Material.SPLASH_POTION);
					PotionMeta strengthingMeta = (PotionMeta) strengthingPotion.getItemMeta();
					strengthingMeta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3600, 0), true);
					strengthingPotion.setItemMeta(strengthingMeta);
					
					ThrownPotion thrownStrengthPotion = player.launchProjectile(ThrownPotion.class);
					thrownStrengthPotion.setItem(strengthingPotion);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3600, 0));
				}
			}
		}
		else if (event.getItem().getItemMeta().equals(firePotion.getItemMeta())) {
			if (potionCooldown.containsKey(player.getName())) {
				if (potionCooldown.get(player.getName()) > System.currentTimeMillis()) {
					long secondsLeft = (potionCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.sendMessage(ChatColor.DARK_AQUA + "Spell is on cooldown for " + secondsLeft + " seconds.");
					return;
				}
			}
			
			potionCooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
			
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					ItemStack firingPotion = new ItemStack(Material.SPLASH_POTION);
					PotionMeta firingMeta = (PotionMeta) firingPotion.getItemMeta();
					firingMeta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 0), true);
					firingPotion.setItemMeta(firingMeta);
					
					ThrownPotion thrownFirePotion = player.launchProjectile(ThrownPotion.class);
					thrownFirePotion.setItem(firingPotion);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 0));
				}
			}
		}
	}
	
	//BAKER CLASS
	@EventHandler
	public static void onBakerClassRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfBakerClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					// BAKER STARTER ITEMS
					player.getInventory().addItem(ItemManager.dwarfBakerBook);
					player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
					player.getInventory().addItem(new ItemStack(Material.FURNACE, 2));
					player.getInventory().addItem(new ItemStack(Material.CHEST, 2));
					player.getInventory().addItem(new ItemStack(Material.CLAY, 10));
					player.getInventory().addItem(new ItemStack(Material.OAK_SIGN, 3));
					player.getInventory().addItem(new ItemStack(Material.BRICKS, 64));
					player.getInventory().addItem(new ItemStack(Material.CAKE, 10));
					player.getInventory().addItem(new ItemStack(Material.COAL, 32));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					ruinsSpawn.setWorld(ruinsWorld);
					
					// -------------------------------------------
					// CHANGE DEPENDING ON THE MAP
					if (player.getWorld() == plainsWorld) {
						player.teleport(plainsSpawn);
					}
					else if (player.getWorld() == mountainWorld) {
						player.teleport(mountainSpawn);
					}
					else if (player.getWorld() == desertWorld) {
						player.teleport(desertSpawn);
					}
					else if (player.getWorld() == ruinsWorld) {
						player.teleport(ruinsSpawn);
					}
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfBakerClass);
					
					player.sendMessage(ChatColor.DARK_AQUA + "You have become a Baker Dwarf.");
					player.setDisplayName(player.getName() + ChatColor.DARK_AQUA + " the Baker" + ChatColor.WHITE);
				}
			}
		}
	}
	
	@EventHandler
	public void onBakerBookRightClick(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dwarfBakerBook.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					ItemStack brickCheck = new ItemStack(Material.BRICK);
					
					ThreadLocalRandom randCookieCount = ThreadLocalRandom.current();
					int low = 8;
					int high = 16;
					int cookieResult = randCookieCount.nextInt(high - low) + low;
					
					ThreadLocalRandom randCount = ThreadLocalRandom.current();
					int num = randCount.nextInt(5);
					
					ThreadLocalRandom randCakeCount = ThreadLocalRandom.current();
					int cakeLow = 1;
					int cakeHigh = 6;
					int cakeResult = randCakeCount.nextInt(cakeHigh - cakeLow) + cakeLow;

					if (player.getInventory().containsAtLeast(brickCheck, 10)) {
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.CLAY, 5));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.COOKIE, cookieResult));
						
						if (num == 3) {
							player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.CAKE, cakeResult));
						}
						
						player.getInventory().removeItem(new ItemStack(Material.BRICK, 10));
						player.giveExp(5);
					}
					else if (!player.getInventory().containsAtLeast(brickCheck, 10)) {
						player.sendMessage(ChatColor.DARK_AQUA + "You do not have 10 bricks in your inventory! Smelt clay to get bricks.");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDragonWarriorGiven(PlayerInteractEvent event) {
		if (event.getItem().getItemMeta().equals(ItemManager.dragonWarriorClass.getItemMeta())) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (event.getItem() != null) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					isDragonWarrior.add(player.getUniqueId());
					
					ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
					sword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
					sword.addEnchantment(Enchantment.KNOCKBACK, 2);
					
					ItemStack bow = new ItemStack(Material.BOW, 1);
					bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
					bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
					
					ItemStack stick = new ItemStack(Material.STICK, 1);
					ItemMeta stickMeta = stick.getItemMeta();
					stick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
					stickMeta.setDisplayName("§4Fus Roh Dah Staff");
					stick.setItemMeta(stickMeta);
					
					ItemStack helmet = new ItemStack(Material.GOLDEN_HELMET, 1);
					helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					helmet.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
					helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
					helmet.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
					
					ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE, 1);
					chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					chestplate.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
					chestplate.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
					chestplate.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
					
					ItemStack leggings = new ItemStack(Material.GOLDEN_LEGGINGS, 1);
					leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					leggings.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
					leggings.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
					leggings.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
					
					ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS, 1);
					boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
					boots.addEnchantment(Enchantment.PROTECTION_FIRE, 4);
					boots.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
					boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
					boots.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
					
					ItemStack healingPotion = new ItemStack(Material.POTION, 1);
					PotionMeta healingMeta = (PotionMeta)healingPotion.getItemMeta();
					healingMeta.setBasePotionData(new PotionData(PotionType.REGEN, false, false));
					healingPotion.setItemMeta(healingMeta);
					
					ItemStack speedPotion = new ItemStack(Material.POTION, 1);
					PotionMeta speedMeta = (PotionMeta)speedPotion.getItemMeta();
					speedMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, false));
					speedPotion.setItemMeta(speedMeta);
					
					ItemStack strengthPotion = new ItemStack(Material.POTION, 1);
					PotionMeta strengthMeta = (PotionMeta)strengthPotion.getItemMeta();
					strengthMeta.setBasePotionData(new PotionData(PotionType.STRENGTH, false, false));
					strengthPotion.setItemMeta(strengthMeta);
					
					ItemStack firePotion = new ItemStack(Material.POTION, 1);
					PotionMeta fireMeta = (PotionMeta)firePotion.getItemMeta();
					fireMeta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE, false, false));
					firePotion.setItemMeta(fireMeta);
					
					// DRAGON WARRIOR ITEMS
					player.getInventory().addItem(stick);
					player.getInventory().addItem(sword);
					player.getInventory().addItem(bow);
					player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 24));
					player.getInventory().addItem(healingPotion);
					player.getInventory().addItem(speedPotion);
					player.getInventory().addItem(strengthPotion);
					player.getInventory().addItem(firePotion);
					player.getInventory().addItem(new ItemStack(Material.ARROW, 128));
					
					player.getInventory().setHelmet(helmet);
					player.getInventory().setChestplate(chestplate);
					player.getInventory().setLeggings(leggings);
					player.getInventory().setBoots(boots);
					
					player.giveExp(315);
					
					player.getInventory().removeItem(ItemManager.dragonWarriorClass);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2400, 0));
					
					player.sendMessage(ChatColor.GOLD + "You have become a the Dragon Warrior.");
					player.setDisplayName(player.getName() + ChatColor.GOLD + " the Dragon Warrior" + ChatColor.WHITE);
				}
			}
		}
	}
}
