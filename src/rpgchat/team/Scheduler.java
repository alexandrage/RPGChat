package rpgchat.team;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import rpgchat.Main;
import rpgchat.packet.WrapperPlayServerScoreboardTeam;

public class Scheduler extends BukkitRunnable {
	private Main plugin;
	private static String version;
	private static boolean isNew;

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
		if (isNew) {
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
	}

	private WrapperPlayServerScoreboardTeam sendTeam(Player player, int i) {
		WrapperPlayServerScoreboardTeam team = new WrapperPlayServerScoreboardTeam();
		BaseComponent[] c = TextComponent.fromLegacyText(trim(this.plugin.chat.getPlayerPrefix(player)));
		String pref = trim(this.plugin.chat.getPlayerPrefix(player));
		BaseComponent[] temp = TextComponent.fromLegacyText(pref);
		team.setColor(ChatColor.getByChar(temp[temp.length - 1].getColor().toString().substring(1)));
		team.setName(trimt(c[c.length - 1].getColor().toString().substring(1) + player.getName()));
		team.setDisplayName(WrappedChatComponent.fromText(player.getName()));
		team.setMode(i);
		team.setNameTagVisibility("ALWAYS");
		team.setPrefix(WrappedChatComponent.fromText(trim(this.plugin.chat.getPlayerPrefix(player))));
		team.setSuffix(WrappedChatComponent.fromText(trim(this.plugin.chat.getPlayerSuffix(player))));
		team.setPackOptionData(1);
		team.getPlayers().add(player.getName());
		return team;
	}

	private String trim(String name) {
		String color = ChatColor.translateAlternateColorCodes('&', name);
		if (color.length() > 64)
			return color.substring(0, 64);
		return color;
	}

	private String trimt(String name) {
		String color = ChatColor.translateAlternateColorCodes('&', name);
		if (color.length() > 16)
			return color.substring(0, 16);
		return color;
	}

	static {
		version = Bukkit.getServer().getClass().getName().split("\\.")[3];
		try {
			Class.forName("net.minecraft.server." + version + ".EnumChatFormat");
			Class.forName("net.minecraft.server." + version + ".EnumChatFormat");
			isNew = true;
		} catch (ClassNotFoundException e1) {
			isNew = false;
		}
	}
}