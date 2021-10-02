package main.volt.dvz.commands;

import main.volt.dvz.DvZ;
import main.volt.dvz.events.MonsterEvents;
import main.volt.dvz.items.MonsterItemManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

public class AvirellaDragonCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		BossBar avirllaBar = Bukkit.createBossBar(ChatColor.BLUE + "Avirella" + ChatColor.DARK_PURPLE + " the Swift Dragon", BarColor.PURPLE, BarStyle.SOLID);
		
		for (Player players : Bukkit.getOnlinePlayers()) {
			avirllaBar.addPlayer(players);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				avirllaBar.removePlayer(players);
				MonsterEvents.canSpawn = true;
			}, 6000);
		}
		
		DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.ENDER_DRAGON, true));
		DisguiseAPI.setViewDisguiseToggled(player, false);
		DisguiseAPI.setActionBarShown(player, false);
		
		player.setDisplayName(ChatColor.BLUE + "Avirella" + ChatColor.DARK_PURPLE + " the Swift Dragon" + ChatColor.WHITE);
		player.setFlying(true);
		
		player.getInventory().addItem(MonsterItemManager.lightningStick);
		player.getInventory().addItem(MonsterItemManager.dwarfLauncher);
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
