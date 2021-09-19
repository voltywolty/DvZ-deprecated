package main.volt.dvz.events;

import main.volt.dvz.DvZ;
import main.volt.dvz.items.ItemManager;
import me.libraryaddict.disguise.DisguiseAPI;

import java.util.HashMap;
import java.util.List;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

// THIS CLASS IS FOR DWARVES ONLY! MONSTERS ARE TO BE HANDLED WITH IN MonsterEvents

public class ClassEvents implements Listener {
	
	// PLAINS WORLD
	static World plainsWorld = Bukkit.getServer().getWorld("dwarf_plains");
	static Location plainsSpawn = new Location(plainsWorld, 1487, 72, -148); // DWARF SPAWN
	
	// MOUNTAIN WORLD
	static World mountainWorld = Bukkit.getServer().getWorld("dwarf_mountain");
	static Location mountainSpawn = new Location(mountainWorld, -152, 116, 498);
	
	// DESERT WORLD
	static World desertWorld = Bukkit.getServer().getWorld("dwarf_desert");
	static Location desertSpawn = new Location(desertWorld, 91, 69, 449); // DWARF SPAWN
	
	// THIS IS FOR THE CLASS GIVER (MAGMA CREAM)
	@EventHandler
	public static void onClassGiverRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.classSelector.getItemMeta())) {
					
					Random rand = new Random();
					int classChance = rand.nextInt(8);
					
					player.getInventory().addItem(ItemManager.dwarfBuilderClass); // This is guaranteed no matter what
					
					if (classChance == 2) {
						player.getInventory().addItem(ItemManager.dwarfBlacksmithClass);
					}
					else if (classChance == 3) {
						player.getInventory().addItem(ItemManager.dwarfTailorClass);
					}
					else if (classChance == 4) {
						player.getInventory().addItem(ItemManager.dwarfBakerClass);
					}
					else if (classChance == 5) {
						player.getInventory().addItem(ItemManager.dwarfAlchemistClass);
					}
					
					player.getInventory().removeItem(ItemManager.classSelector);
					player.sendMessage(ChatColor.BLUE + "You have been given classes. Left click a disc to become that class.");
					
					player.setFallDistance(3);
					player.setCanPickupItems(true);
					DisguiseAPI.undisguiseToAll(player);
				}
			}
		}
	}
	
	// BUILDER CLASS
	@EventHandler
	public static void onBuilderClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfBuilderClass.getItemMeta())) {
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
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfBuilderClass);
					
					player.sendMessage(ChatColor.BLUE + "You have become a Builder Dwarf.");
					player.setDisplayName(player.getName() + " §3the Builder§");
				}
			}
		}
	}
	
	HashMap<String, Long> bookCooldown = new HashMap<String, Long>();
	
	@EventHandler
	public void onBuilderBookRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfBuilderBook.getItemMeta())) {
					Player player = event.getPlayer();
					
					if (bookCooldown.containsKey(player.getName())) {
						if (bookCooldown.get(player.getName()) > System.currentTimeMillis()) {
							long secondsLeft = (bookCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
							player.sendMessage(ChatColor.BLUE + "Spell is on cooldown for " + secondsLeft + " seconds.");
							return;
						}
					}
					
					bookCooldown.put(player.getName(), System.currentTimeMillis() + (30 * 1000));
					
					List <String> buildItems = DvZ.getInstance().getConfig().getStringList("BuilderBlocks");
					int index = new Random().nextInt(buildItems.size());
					String items = buildItems.get(index);
					
					ItemStack newItem = new ItemStack(Material.getMaterial(items.toUpperCase()), 64);
					
					player.getWorld().dropItem(player.getEyeLocation(), newItem);
					player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.GLOWSTONE_DUST, 3));
				}
			}
		}
	}
	
	// BLACKSMITH CLASS
	@EventHandler
	public static void onBlacksmithClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfBlacksmithClass.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.getInventory().clear();
					
					// BLACKSMITH STARTER ITEMS
					player.getInventory().addItem(ItemManager.dwarfBlacksmithBook);
					player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
					player.getInventory().addItem(new ItemStack(Material.REDSTONE, 8));
					player.getInventory().addItem(new ItemStack(Material.GOLD_ORE, 24));
					player.getInventory().addItem(new ItemStack(Material.FURNACE, 4));
					player.getInventory().addItem(new ItemStack(Material.COAL, 10));
					player.getInventory().addItem(new ItemStack(Material.CHEST, 2));
					player.getInventory().addItem(new ItemStack(Material.OAK_SIGN, 3));
					player.getInventory().addItem(new ItemStack(Material.NETHER_BRICKS, 64));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					
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
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfBlacksmithClass);
					
					player.sendMessage(ChatColor.BLUE + "You have become a Blacksmith Dwarf.");
					player.setDisplayName(player.getName() + " §3the Blacksmith§");
				}
			}
		}
	}
	
	@EventHandler
	public void onBlacksmithBookRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfBlacksmithBook.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					ItemStack clockCheck = new ItemStack(Material.CLOCK);
					if (player.getInventory().containsAtLeast(clockCheck, 3)) {
						List <String> toolItems = DvZ.getInstance().getConfig().getStringList("BlacksmithTools");
						int index = new Random().nextInt(toolItems.size());
						String items = toolItems.get(index);
						
						ItemStack newItem = new ItemStack(Material.getMaterial(items.toUpperCase()));
						
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.COAL, 18));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.ARROW, 20));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.BOW, 1));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.REDSTONE, 12));
						player.getWorld().dropItem(player.getEyeLocation(), newItem);
						
						player.getInventory().removeItem(new ItemStack(Material.CLOCK, 3));
					}
					else if (!player.getInventory().containsAtLeast(clockCheck, 3)) {
						player.sendMessage(ChatColor.BLUE + "You do not have three clocks in your inventory! Smelt gold and redstone and craft them into clocks.");
					}
				}
			}
		}
	}
	
	// TAILOR CLASS
	@EventHandler
	public static void onTailorClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfTailorClass.getItemMeta())) {
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
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfTailorClass);
					
					player.sendMessage(ChatColor.BLUE + "You have become a Tailor Dwarf.");
					player.setDisplayName(player.getName() + " §3the Tailor§");
				}
			}
		}
	}
	
	@EventHandler
	public void onTailorBookRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfTailorBook.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					if (bookCooldown.containsKey(player.getName())) {
						if (bookCooldown.get(player.getName()) > System.currentTimeMillis()) {
							long secondsLeft = (bookCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
							player.sendMessage(ChatColor.BLUE + "Spell is on cooldown for " + secondsLeft + " seconds.");
							return;
						}
					}
					
					bookCooldown.put(player.getName(), System.currentTimeMillis() + (10 * 1000));
					
					ItemStack dyeCheck = new ItemStack(Material.ORANGE_DYE);

					if (player.getInventory().containsAtLeast(dyeCheck, 10)) {
						List <String> armorItems = DvZ.getInstance().getConfig().getStringList("TailorArmors");
						int index = new Random().nextInt(armorItems.size());
						String items = armorItems.get(index);
						
						ItemStack newItem = new ItemStack(Material.getMaterial(items.toUpperCase()));
						
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.GOLD_ORE, 10));
						player.getWorld().dropItem(player.getEyeLocation(), newItem);
						
						player.getInventory().removeItem(new ItemStack(Material.ORANGE_DYE, 10));
					}
					else if (!player.getInventory().containsAtLeast(dyeCheck, 10)) {
						player.sendMessage(ChatColor.BLUE + "You do not have 10 orange dye in your inventory! Use bonemeal and craft orange dye.");
					}
				}
			}
		}
	}
	
	//ALCHEMIST CLASS
	@EventHandler
	public static void onAlchemistClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfAlchemistClass.getItemMeta())) {
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
					player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 2));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					
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
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfAlchemistClass);
					
					player.sendMessage(ChatColor.BLUE + "You have become a Alchemist Dwarf.");
					player.setDisplayName(player.getName() + " §3the Alchemist§");
				}
			}
		}
	}
	
	@EventHandler
	public void onAlchemistBookRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfAlchemistBook.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					if (bookCooldown.containsKey(player.getName())) {
						if (bookCooldown.get(player.getName()) > System.currentTimeMillis()) {
							long secondsLeft = (bookCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
							player.sendMessage(ChatColor.BLUE + "Spell is on cooldown for " + secondsLeft + " seconds.");
							return;
						}
					}
					
					bookCooldown.put(player.getName(), System.currentTimeMillis() + (15 * 1000));
					
					ItemStack potionCheck = new ItemStack(Material.POTION);
					PotionMeta potionMeta = (PotionMeta)potionCheck.getItemMeta();
					potionMeta.setBasePotionData(new PotionData(PotionType.MUNDANE));
					potionCheck.setItemMeta(potionMeta);

					if (player.getInventory().containsAtLeast(potionCheck, 4)) {		
						Random rand = new Random();
						int chance = rand.nextInt(3);
						
						ItemStack healthPotion = new ItemStack(Material.POTION, 3);
						PotionMeta healthMeta = (PotionMeta)healthPotion.getItemMeta();
						healthMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, false));
						healthPotion.setItemMeta(healthMeta);
						
						ItemStack speedPotion = new ItemStack(Material.POTION, 3);
						PotionMeta speedMeta = (PotionMeta)speedPotion.getItemMeta();
						speedMeta.setBasePotionData(new PotionData(PotionType.SPEED, false, false));
						speedPotion.setItemMeta(speedMeta);
						
						ItemStack strengthPotion = new ItemStack(Material.POTION, 3);
						PotionMeta strengthMeta = (PotionMeta)strengthPotion.getItemMeta();
						strengthMeta.setBasePotionData(new PotionData(PotionType.STRENGTH, false, false));
						strengthPotion.setItemMeta(strengthMeta);
						
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.BONE, 3));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.MILK_BUCKET, 1));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.SAND, 3));
						
						if (chance == 0) {
							player.getWorld().dropItem(player.getEyeLocation(), healthPotion);
						}
						else if (chance == 1) {
							player.getWorld().dropItem(player.getEyeLocation(), speedPotion);
						}
						else if (chance == 2) {
							player.getWorld().dropItem(player.getEyeLocation(), strengthPotion);
						}
						
						player.getInventory().removeItem(potionCheck);
						player.getInventory().removeItem(potionCheck);
						player.getInventory().removeItem(potionCheck);
						player.getInventory().removeItem(potionCheck);
					}
					else if (!player.getInventory().containsAtLeast(potionCheck, 4)) {
						player.sendMessage(ChatColor.BLUE + "You do not have 4 mundane potions in your inventory! Fill your brew your bottles of water to get mundane potions.");
					}
				}
			}
		}
	}
	
	//BAKER CLASS
	@EventHandler
	public static void onBakerClassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfBakerClass.getItemMeta())) {
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
					player.getInventory().addItem(new ItemStack(Material.COAL, 25));
					
					plainsSpawn.setWorld(plainsWorld);
					mountainSpawn.setWorld(mountainWorld);
					desertSpawn.setWorld(desertWorld);
					
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
					// -------------------------------------------
					player.getInventory().removeItem(ItemManager.dwarfBakerClass);
					
					player.sendMessage(ChatColor.BLUE + "You have become a Baker Dwarf.");
					player.setDisplayName(player.getName() + " §3the Baker§");
				}
			}
		}
	}
	
	@EventHandler
	public void onBakerBookRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getItemMeta().equals(ItemManager.dwarfBakerBook.getItemMeta())) {
					Player player = event.getPlayer();
					
					player.updateInventory();
					
					ItemStack brickCheck = new ItemStack(Material.BRICK);
					
					Random randCookieCount = new Random();
					int low = 10;
					int high = 22;
					int cookieResult = randCookieCount.nextInt(high - low) + low;
					
					Random randCount = new Random();
					int num = randCount.nextInt(5);
					
					Random randCakeCount = new Random();
					int cakeLow = 2;
					int cakeHigh = 8;
					int cakeResult = randCakeCount.nextInt(cakeHigh - cakeLow) + cakeLow;

					if (player.getInventory().containsAtLeast(brickCheck, 15)) {
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.CLAY, 5));
						player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.COOKIE, cookieResult));
						
						if (num == 3) {
							player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.CAKE, cakeResult));
						}
						
						player.getInventory().removeItem(new ItemStack(Material.BRICK, 15));
					}
					else if (!player.getInventory().containsAtLeast(brickCheck, 15)) {
						player.sendMessage(ChatColor.BLUE + "You do not have 15 bricks in your inventory! Smelt clay to get bricks.");
					}
				}
			}
		}
	}
}
