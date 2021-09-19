package main.volt.dvz.events;

import main.volt.dvz.DvZ;
import main.volt.dvz.commands.StartCommand;
import main.volt.dvz.items.MonsterItemManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvents implements Listener {
	
	@SuppressWarnings("unused")
	private DvZ plugin;
	
	public PlayerEvents(DvZ dvz) {
		plugin = dvz;
	}
	
	@EventHandler
	public static void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		boolean hasJoined = player.hasPlayedBefore();
		
		if (!hasJoined) {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome " + player.getName() + " to the official DvZ server!");
		}
		
		if (StartCommand.gameStarted) {
			player.getInventory().addItem(MonsterItemManager.monsterClassSelector);
		}
	}
}
