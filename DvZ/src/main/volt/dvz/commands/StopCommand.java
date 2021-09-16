package main.volt.dvz.commands;

import main.volt.dvz.DvZ;
import me.libraryaddict.disguise.DisguiseAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StopCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		if (StartCommand.gameStarted) {
			for (Player players : Bukkit.getOnlinePlayers()) {
				players.getInventory().clear();
				players.sendMessage(ChatColor.BLUE + "The game has ended! Please wait for the next round.");
				players.setDisplayName(players.getName());
				player.setFallDistance(3);
				player.setCanPickupItems(true);
				
				DisguiseAPI.undisguiseToAll(player);
				
				players.teleport(players.getWorld().getSpawnLocation());
				
				StartCommand.gameStarted = false;
			}
		}
		else {
			player.sendMessage(ChatColor.RED + "Game has already ended. Please start a new game!");
		}
	}

	@Override
	public String name() {
		return plugin.commandManager.stop;
	}

	@Override
	public String info() {
		return "Stops the DvZ game.";
	}

	@Override
	public String[] aliases() {
		return new String[0];
	}
}
