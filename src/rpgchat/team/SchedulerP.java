package rpgchat.team;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import rpgchat.Main;

public class SchedulerP extends BukkitRunnable {
	Main plugin;

	public SchedulerP(Main team) {
		this.plugin = team;
	}

	@Override
	public void run() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			String now = this.plugin.u.replaceList(
					this.plugin.getConfig().getString("formatplayerlist", "{prefix}{player}{suffix}"), player);
			player.setPlayerListName(now);
		}
	}
}