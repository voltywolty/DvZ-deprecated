package main.volt.dvz.commands;

import main.volt.dvz.DvZ;
import main.volt.dvz.items.ItemManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();
	public static boolean gameStarted = false;
	
	@Override
	public void onCommand(Player player, String[] args) {
		if (!gameStarted) {
			for (Player players : Bukkit.getOnlinePlayers()) {
				gameStarted = true;
				players.getInventory().addItem(ItemManager.classSelector);
				players.sendMessage(ChatColor.GOLD + "The game has started! You have one day to build a keep before the drag and monsters arrive.");
			}
		}
		else {
			player.sendMessage(ChatColor.RED + "Game has already start. Please end the game to start a new round.");
		}
	}

	@Override
	public String name() {
		return plugin.commandManager.start;
	}

	@Override
	public String info() {
		return "Starts the DvZ game.";
	}

	@Override
	public String[] aliases() {
		return new String[0];
	}
}
