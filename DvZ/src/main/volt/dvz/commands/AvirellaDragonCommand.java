package main.volt.dvz.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import main.volt.dvz.DvZ;
import main.volt.dvz.items.MonsterItemManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

public class AvirellaDragonCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.ENDER_DRAGON, true));
		DisguiseAPI.setViewDisguiseToggled(player, false);
		DisguiseAPI.setActionBarShown(player, false);
		
		player.setDisplayName(ChatColor.BLUE + "Avirella" + ChatColor.DARK_PURPLE + " the Swift Dragon" + ChatColor.WHITE);
		player.setGameMode(GameMode.CREATIVE);
		player.setFlySpeed(5);
		
		player.getInventory().addItem(MonsterItemManager.lightningStick);
		// Need to add the stick that sends dwarves in the air!
	}

	@Override
	public String name() {
		return plugin.commandManager.avirella;
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
