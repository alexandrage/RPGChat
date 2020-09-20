package rpgchat.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import net.ess3.api.IEssentials;
import rpgchat.Main;

public class EventSendListener implements Listener {
	private Main plugin;

	public EventSendListener(Main instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void on(PlayerCommandSendEvent e) {
		if (this.plugin.ess != null) {
			IEssentials ess = (IEssentials) this.plugin.ess;
			e.getCommands().removeIf(name -> {
				Player player = Bukkit.getPlayerExact(name);
				if (player == null) {
					return false;
				}
				return ess.getUser(player.getName()).isVanished();
			});
		}
	}
}