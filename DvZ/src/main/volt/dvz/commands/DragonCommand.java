package main.volt.dvz.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import main.volt.dvz.DvZ;
import main.volt.dvz.items.MonsterItemManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

public class DragonCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.ENDER_DRAGON, true));
		DisguiseAPI.setViewDisguiseToggled(player, false);
		DisguiseAPI.setActionBarShown(player, false);
		
		player.setDisplayName(ChatColor.RED + "Vlaurunga" + ChatColor.DARK_PURPLE + " the Mighty Dragon" + ChatColor.WHITE);
		player.setGameMode(GameMode.CREATIVE);
		
		player.getInventory().addItem(MonsterItemManager.dragonFireball);
		player.getInventory().addItem(MonsterItemManager.lightningStick);
	}

	@Override
	public String name() {
		return plugin.commandManager.dragon;
	}

	@Override
	public String info() {
		return "Starts the beginning of the monster summoning.";
	}

	@Override
	public String[] aliases() {
		return new String[0];
	}

}
