package rpgchat.team;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import rpgchat.Main;
import rpgchat.packet.WrapperPlayServerScoreboardTeam;

public class Scheduler extends BukkitRunnable {
	Main plugin;

	public Scheduler(Main team) {
		this.plugin = team;
	}

	@Override
	public void run() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player p : players) {
			setTeam(p, players);
		}
	}

	private void setTeam(Player player, Collection<? extends Player> players) {
		WrapperPlayServerScoreboardTeam remove = sendTeam(player, 1);
		WrapperPlayServerScoreboardTeam created = sendTeam(player, 0);
		for (Player p : players) {
			if (this.plugin.getConfig().getBoolean("tabworld")) {
				try {
					if (player.getWorld() == p.getWorld()) {
						player.showPlayer(this.plugin, p);
					} else {
						player.hidePlayer(this.plugin, p);
					}
				} catch (Exception e) {
				}
			}
			remove.sendPacket(p);
			created.sendPacket(p);
		}

	}

	private WrapperPlayServerScoreboardTeam sendTeam(Player player, int i) {
		WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		BaseComponent[] c = TextComponent.fromLegacyText(trim(this.plugin.chat.getPlayerPrefix(player)));
		team.setName(trim(c[c.length - 1].getColor().toString().substring(1) + player.getName()));
		team.setDisplayName(player.getName());
		team.setMode(i);
		team.setNameTagVisibility("ALWAYS");
		team.setPrefix(trim(this.plugin.chat.getPlayerPrefix(player)));
		team.setSuffix(trim(this.plugin.chat.getPlayerSuffix(player)));
		team.setPackOptionData(1);
		team.getPlayers().add(player.getName());
		return team;
	}

	private String trim(String name) {
		String color = ChatColor.translateAlternateColorCodes('&', name);
		if (color.length() > 16)
			return color.substring(0, 16);
		return color;
	}
}