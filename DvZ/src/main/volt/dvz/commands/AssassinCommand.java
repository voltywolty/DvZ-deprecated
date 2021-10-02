package main.volt.dvz.commands;

import main.volt.dvz.DvZ;
import main.volt.dvz.events.MonsterEvents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AssassinCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();

	@Override
	public void onCommand(Player player, String[] args) {
		Player selectedPlayer = Bukkit.getOnlinePlayers().stream().findAny().get();
		
		for (Player allPlayers : Bukkit.getOnlinePlayers()) {
			allPlayers.sendMessage(ChatColor.DARK_RED + "There is an assassin! Locate the assassin and kill them before it's too late.");
		}
		selectedPlayer.sendMessage(ChatColor.YELLOW + "You have become the assassin! Kill as many dwarves as you can before its too late.");
		
		MonsterEvents.canSpawn = true;
	}
	
	@Override
	public String name() {
		return plugin.commandManager.assassin;
	}

	@Override
	public String info() {
		return "Selects a random dwarf to become an assassin.";
	}

	@Override
	public String[] aliases() {
		return new String[0];
	}

}
