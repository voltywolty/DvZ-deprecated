package main.volt.dvz.commands;

import main.volt.dvz.DvZ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
	private ArrayList<SubCommand> commands = new ArrayList<SubCommand>();
	private DvZ plugin = DvZ.getInstance();
	
	public CommandManager() {
		
	}
	
	// SUB COMMANDS
	public String main = "dvz";
	public String help = "help";
	public String start = "start";
	public String stop = "stop";
	public String dragon = "dragon";
			
	public void setup() {
		plugin.getCommand(main).setExecutor(this);
		
		this.commands.add(new StartCommand());
		this.commands.add(new StopCommand());
		this.commands.add(new DragonCommand());
		this.commands.add(new HelpCommand());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (command.getName().equalsIgnoreCase(main) && player.isOp()) {
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "Unknown arguments. Type /dvz help for more information.");
				return true;
			}
			
			SubCommand target = this.get(args[0]);
			
			if (target == null) {
				player.sendMessage(ChatColor.RED + "Unknown arguments. Type /dvz help for more information.");
				return true;
			}
			
			ArrayList<String> arrayList = new ArrayList<String>();
			
			arrayList.addAll(Arrays.asList(args));
			arrayList.remove(0);
			
			try {
				target.onCommand(player, args);
			}
			catch (Exception e) {
				player.sendMessage(ChatColor.RED + "An error has occured!");
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	private SubCommand get(String name) {
		Iterator<SubCommand> subcommands = this.commands.iterator();
		
		while(subcommands.hasNext()) {
			SubCommand sc = (SubCommand) subcommands.next();
			
			if (sc.name().equalsIgnoreCase(name)) {
				return sc;
			}
			
			String[] aliases;
			int length = (aliases = sc.aliases()).length;
			
			for (int var5 = 0; var5 < length; var5++) {
				String alias = aliases[var5];
				
				if (name.equalsIgnoreCase(alias)) {
					return sc;
				}
			}
		}
		return null;
	}
}
