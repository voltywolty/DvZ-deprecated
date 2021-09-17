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
		
		if (player.isOp()) {
			player.sendMessage(ChatColor.YELLOW + "dragon - Play as the dragon before the monsters spawn in. You want to start after the first day.");
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