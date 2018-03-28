package rpgchat.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.ess3.api.IEssentials;
import rpgchat.Main;

public class MsgListener implements CommandExecutor, TabCompleter {
	Main plugin;

	public MsgListener(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		ArrayList<String> entity = new ArrayList<String>();
		if ((args.length == 1)) {
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			int j;
			int i;
			if (!args[0].equals("")) {
				j = players.size();
				for (i = 0; i < j; i++) {
					Player[] p = players.toArray(new Player[] {});
					boolean isHide = false;
					if (this.plugin.ess != null) {
						IEssentials ess = (IEssentials) this.plugin.ess;
						isHide = ess.getUser(p[i]).isHidden();
					}
					if (!isHide) {
						String name = p[i].getName();
						if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
							entity.add(name);
						}
					}
				}
			} else {
				j = players.size();
				for (i = 0; i < j; i++) {
					Player[] p = players.toArray(new Player[] {});
					String name = p[i].getName();
					entity.add(name);
				}
			}
			Collections.sort(entity);
			return entity;
		}
		return entity;
	}
}