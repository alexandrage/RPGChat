package rpgchat.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.ess3.api.IEssentials;

public class EssUtils {
	public static boolean isSocialSpyEnabled(Plugin plugin, Player player) {
		IEssentials ess = (IEssentials) plugin;
		return ess.getUser(player).isSocialSpyEnabled();
	}
	
	public static boolean isIgnoredPlayer(Plugin plugin, Player player, Player player2) {
		IEssentials ess = (IEssentials) plugin;
		return ess.getUser(player).isIgnoredPlayer(ess.getUser(player2));
	}
	
	public static boolean isHidden(Plugin plugin, Player player) {
		IEssentials ess = (IEssentials) plugin;
		return ess.getUser(player).isHidden();
	}
}