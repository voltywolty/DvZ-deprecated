package main.volt.dvz.commands;

import main.volt.dvz.items.ItemManager;
import main.volt.dvz.items.MonsterItemManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only admins can use that command!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("givedwarfclass") && player.isOp()) {
			player.getInventory().addItem(ItemManager.classSelector);
		}
		else if (!player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
		}
		
		if (cmd.getName().equalsIgnoreCase("givemonsterclass") && player.isOp()) {
			player.getInventory().addItem(MonsterItemManager.monsterClassSelector);
		}
		else if (!player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
		}
		
		if (cmd.getName().equalsIgnoreCase("givedragonwarrior") && player.isOp()) {
			player.getInventory().addItem(ItemManager.dragonWarriorClass);
		}
		else if (!player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
		}
		
		return true;
	}
	
}
