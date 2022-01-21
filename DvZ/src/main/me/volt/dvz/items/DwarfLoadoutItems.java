package main.me.volt.dvz.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

// NOTE - an item loader class needs to be made
// that takes in custom class data and returns
// an item stack - however, I can store this data
// in .yml files that can be loaded and parsed
public class DwarfLoadoutItems {
    public static ItemStack divider;
    public static ItemStack leftArrow, rightArrow;

    public static ItemStack pointsRemaining;

    // NOTE - for best optimization, match melees with
    // the same line as dwarvenRuneblade and match others
    // with the corresponding types
    public static ItemStack dwarvenRunebladeEquipped;
    public static ItemStack dwarvenShortbowEquipped;
    public static ItemStack jimmyJuiceEquipped;
    public static ItemStack dwarvenArmorEquipped;
    public static ItemStack magicStone1Equipped;
    public static ItemStack enchantedLamps1Equipped;
    public static ItemStack wigglyWrenches1Equipped;
    public static ItemStack stoneMason1Equipped;
    public static ItemStack torchbearer1Equipped;

    // ITEMS TO SHOW IN GENERAL
    public static ItemStack dwarvenRuneblade;

    public static void init() {
        createDivider();
        createLeftArrow();
        createRightArrow();

        createPointsRemaining();

        createRunebladeEquipped();
        createRuneblade();

        createShortbowEquipped();
        createJimmyJuiceEquipped();
        createDwarvenArmorEquipped();
        createMagicStone1Equipped();
        createEnchantedLamps1Equipped();
        createWigglyWrenches1Equipped();
        createStoneMason1Equipped();
        createTorchbearer1Equipped();
    }

    private static void createPointsRemaining() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT, 64);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bPoints Remaining");

        item.setItemMeta(meta);
        pointsRemaining = item;
    }

    private static void createDivider() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");

        item.setItemMeta(meta);
        divider = item;
    }

    private static void createLeftArrow() {
        ItemStack item = new ItemStack(Material.PUMPKIN_SEEDS, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bPrevious Page");

        item.setItemMeta(meta);
        leftArrow = item;
    }

    private static void createRightArrow() {
        ItemStack item = new ItemStack(Material.MELON_SEEDS, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bNext Page");

        item.setItemMeta(meta);
        rightArrow = item;
    }

    private static void createRunebladeEquipped() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE, 16);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Runeblade");

        List<String> lore = new ArrayList<>();
        lore.add("§a16 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§eType: §bMelee Weapon");

        lore.add(" ");
        lore.add("§5§oForged by the great Dwarven blacksmith");
        lore.add("§5§oGoldfounder Pause, this Dwarven Runeblade");
        lore.add("§5§ois made out of ancient clocks and dyed");
        lore.add("§5§owith the blood of Ice Dragon Yrvader,");
        lore.add("§5§oCrassle's late father.");
        lore.add(" ");

        lore.add("§c§lLoadouts can only have 1 melee weapon!");
        lore.add("§0§kDwarven Runeblade");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dwarvenRunebladeEquipped = item;
    }

    private static void createRuneblade() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE, 16);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Runeblade");

        List<String> lore = new ArrayList<>();
        lore.add("§a16 points");
        lore.add("§d(Click to Add)");
        lore.add(" ");

        lore.add("§eType: §bMelee Weapon");

        lore.add(" ");
        lore.add("§5§oForged by the great Dwarven blacksmith");
        lore.add("§5§oGoldfounder Pause, this Dwarven Runeblade");
        lore.add("§5§ois made out of ancient clocks and dyed");
        lore.add("§5§owith the blood of Ice Dragon Yrvader,");
        lore.add("§5§oCrassle's late father.");
        lore.add(" ");

        lore.add("§0§kDwarven Runeblade");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dwarvenRuneblade = item;
    }

    private static void createShortbowEquipped() {
        ItemStack item = new ItemStack(Material.BOW, 12);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Shortbow");

        List<String> lore = new ArrayList<>();
        lore.add("§a12 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§eType: §bRanged Weapon");

        lore.add(" ");
        lore.add("§5§oI don't believe we can kill them all and I don't");
        lore.add("§5§obelieve there is every going to be another bright");
        lore.add("§5§oday, but as long as I can still craft arrows my");
        lore.add("§5§obow will never stop piercing undead skulls.");
        lore.add("§6- SalogelSureshot");
        lore.add(" ");

        lore.add("§c§lLoadouts can only have 1 ranged weapon!");
        lore.add("§0§kDwarven Shortbow");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dwarvenShortbowEquipped = item;
    }

    private static void createJimmyJuiceEquipped() {
        ItemStack item = new ItemStack(Material.POTION, 8);
        PotionMeta healthMeta = (PotionMeta) item.getItemMeta();
        healthMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, false));
        item.setItemMeta(healthMeta);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bJimmy Juice");

        List<String> lore = new ArrayList<>();
        lore.add("§a8 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§eType: §bSupport Item");

        lore.add(" ");
        lore.add("§eWith this juice, you can automatically heal yourself");
        lore.add("§eto full after taking 5 hearts of damage for 50 mana.");
        lore.add(" ");

        lore.add("§c§lLoadouts can only have 1 support item!");
        lore.add("§0§kJimmy Juice");
        meta.setLore(lore);

        item.setItemMeta(meta);
        jimmyJuiceEquipped = item;
    }

    private static void createDwarvenArmorEquipped() {
        ItemStack item = new ItemStack(Material.DIAMOND, 8);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bDwarven Armor");

        List<String> lore = new ArrayList<>();
        lore.add("§a8 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§eType: §bArmor Item");

        lore.add(" ");
        lore.add("§eDefault Dwarven armor that gives you 10 extra hearts.");
        lore.add(" ");

        lore.add("§c§lLoadouts can only have 1 armor item!");
        lore.add("§0§kDwarven Armor");
        meta.setLore(lore);

        item.setItemMeta(meta);
        dwarvenArmorEquipped = item;
    }

    private static void createMagicStone1Equipped() {
        ItemStack item = new ItemStack(Material.BRICK, 4);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bScrolls of Magic Stone I");

        List<String> lore = new ArrayList<>();
        lore.add("§a4 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§5§oStart with 2 Scrolls of Magic");
        lore.add("§5§oStone that can instantly create");
        lore.add("§5§oan Enchanted Wall in front of you.");
        lore.add(" ");

        lore.add("§k§0Scrolls of Magic Stone I");
        meta.setLore(lore);

        item.setItemMeta(meta);
        magicStone1Equipped = item;
    }

    private static void createEnchantedLamps1Equipped() {
        ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE, 4);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bEnchanted Lamps I");

        List<String> lore = new ArrayList<>();
        lore.add("§a4 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§5§oStart with 12 enchanted lamps,");
        lore.add("§5§owhich provide an unbreakable");
        lore.add("§5§olight source for 60 seconds.");
        lore.add(" ");

        lore.add("§k§0Enchanted Lamps I");
        meta.setLore(lore);

        item.setItemMeta(meta);
        enchantedLamps1Equipped = item;
    }

    private static void createWigglyWrenches1Equipped() {
        ItemStack item = new ItemStack(Material.LIGHT_GRAY_DYE, 4);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bWiggly Wrenches I");

        List<String> lore = new ArrayList<>();
        lore.add("§a4 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§5§oGives you 2 Wiggly Wrenches,");
        lore.add("§5§owhich instantly repair your");
        lore.add("§5§oarmor to full.");
        lore.add(" ");

        lore.add("§k§0Wiggly Wrench I");
        meta.setLore(lore);

        item.setItemMeta(meta);
        wigglyWrenches1Equipped = item;
    }

    private static void createStoneMason1Equipped() {
        ItemStack item = new ItemStack(Material.COBBLESTONE, 2);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bStone Mason I");

        List<String> lore = new ArrayList<>();
        lore.add("§a2 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§5§oStart with 64 stone.");
        lore.add(" ");

        lore.add("§k§0Stone Mason I");
        meta.setLore(lore);

        item.setItemMeta(meta);
        stoneMason1Equipped = item;
    }

    private static void createTorchbearer1Equipped() {
        ItemStack item = new ItemStack(Material.TORCH, 4);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bTorchbearer I");

        List<String> lore = new ArrayList<>();
        lore.add("§a4 points");
        lore.add("§d(Click to Remove)");
        lore.add(" ");

        lore.add("§5§oStart with 24 stone.");
        lore.add(" ");

        lore.add("§k§0Torchbearer I");
        meta.setLore(lore);

        item.setItemMeta(meta);
        torchbearer1Equipped = item;
    }
}
