package main.volt.dvz.commands;

import main.volt.dvz.DvZ;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		player.sendMessage(ChatColor.YELLOW + "List of commands for DvZ are:");
		player.sendMessage(ChatColor.YELLOW + "start - Starts the DvZ game.");
		player.sendMessage(ChatColor.YELLOW + "stop - Ends the DvZ game.");
		player.sendMessage(ChatColor.YELLOW + "monster - Type either start or stop for the monsters to be able to spawn");
		
		if (player.isOp()) {
			player.sendMessage(ChatColor.YELLOW + "avirella - Play as the fast dragon before the monsters spawn in. You want to start after the second night.");
			player.sendMessage(ChatColor.YELLOW + "vlaurunga - Play as the normal dragon before the monsters spawn in. You want to start after the second night.");
		}
	}

	@Override
	public String name() {
		return plugin.commandManager.help;
	}

	@Override
	public String info() {
		return "Lists all DvZ commands";
	}

	@Override
	public String[] aliases() {
		return new String[0];
	}
}
