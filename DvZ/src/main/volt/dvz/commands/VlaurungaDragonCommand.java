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

public class VlaurungaDragonCommand extends SubCommand {
	private DvZ plugin = DvZ.getInstance();
	
	@Override
	public void onCommand(Player player, String[] args) {
		BossBar vlaurungaBar = Bukkit.createBossBar(ChatColor.RED + "Vlaurunga" + ChatColor.DARK_PURPLE + " the Mighty Dragon", BarColor.PURPLE, BarStyle.SOLID);
		
		for (Player players : Bukkit.getOnlinePlayers()) {
			vlaurungaBar.addPlayer(players);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				vlaurungaBar.removePlayer(players);
				MonsterEvents.canSpawn = true;
			}, 6000);
		}
		
		DisguiseAPI.disguiseToAll(player, new MobDisguise(DisguiseType.ENDER_DRAGON, true));
		DisguiseAPI.setViewDisguiseToggled(player, false);
		DisguiseAPI.setActionBarShown(player, false);
		
		player.setDisplayName(ChatColor.RED + "Vlaurunga" + ChatColor.DARK_PURPLE + " the Mighty Dragon" + ChatColor.WHITE);
		player.setFlying(true);
		
		player.getInventory().addItem(MonsterItemManager.dragonFireball);
	}

	@Override
	public String name() {
		return plugin.commandManager.vlaurunga;
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
