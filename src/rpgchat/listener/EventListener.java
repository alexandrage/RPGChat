package rpgchat.listener;

import net.ess3.api.IEssentials;
import rpgchat.Main;
import rpgchat.data.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
	private Main plugin;

	public EventListener(Main instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setGlowing(false);
		this.plugin.u.setUser(p);
		this.plugin.u.sendJoin(p);
		e.setJoinMessage(null);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(PlayerQuitEvent e) {
		this.plugin.u.removeUser(e.getPlayer());
		e.setQuitMessage(null);
		this.plugin.u.sendQuit(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void on(AsyncPlayerChatEvent e) {
		e.setFormat(this.plugin.u.replace(e.getMessage().replace("{&}", "").replace("{!}", "").replace("{?}", ""),
				e.getPlayer()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onm(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		Player p = e.getPlayer();
		this.plugin.u.send(e.getFormat(), p, e.getMessage());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onh(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String mes = e.getMessage();
		String[] s = mes.split(" ");
		String cmd = s[0];
		if ((cmd.equalsIgnoreCase("/rpgchat")) && (p.hasPermission("chat.reload"))) {
			this.plugin.reloadConfig();
			p.sendMessage(this.plugin.getConfig().getString("reloadmsg", "rpgchat перезагружен."));
			e.setCancelled(true);
			return;
		}
		if ((cmd.equalsIgnoreCase("/glow")) && (p.hasPermission("chat.glow"))) {
			User us = this.plugin.u.getUser(p);
			if (us.getGlow()) {
				us.setGlow(false);
				p.setGlowing(false);
			} else {
				us.setGlow(true);
				p.setGlowing(true);
			}
			e.setCancelled(true);
		}
		if (this.plugin.u.e(cmd, new String[] { "/msg", "/w", "/m", "/t", "/pm", "/emsg", "/epm", "/tell", "/etell",
				"/whisper", "/ewhisper" })) {
			e.setCancelled(true);
			if (this.plugin.ess != null) {
				IEssentials ess = (IEssentials) this.plugin.ess;
				if (ess.getUser(p).isMuted()) {
					return;
				}
			}
			this.plugin.u.msgsp(mes, p.getName());
			if (s.length >= 3) {
				Player t = Bukkit.getPlayer(s[1]);
				if (t != null) {
					if (p == t) {
						p.sendMessage(cmd + " <to> <message>");
						return;
					}
					this.plugin.u.sendcmd(mes.substring(s[0].length() + 1 + s[1].length()).replace("{&}", ""), p, t);
					if (this.plugin.ess != null) {
						IEssentials ess = (IEssentials) this.plugin.ess;
						if ((t != null) && (ess.getUser(t).isVanished())) {
							p.sendMessage(this.plugin.u.color(this.plugin.getConfig().getString("msgerr")));
							return;
						}
					}
					this.plugin.u.getUser(p).setReply(t);
					this.plugin.u.getUser(t).setReply(p);
				} else {
					p.sendMessage(this.plugin.u.color(this.plugin.getConfig().getString("msgerr")));
				}
			} else {
				p.sendMessage(cmd + " <to> <message>");
			}
		}
		if (this.plugin.u.e(cmd, new String[] { "/r", "/er", "/reply", "/ereply" })) {
			e.setCancelled(true);
			Player t = this.plugin.u.getUser(p).getReply();
			if (this.plugin.ess != null) {
				IEssentials ess = (IEssentials) this.plugin.ess;
				if (ess.getUser(p).isMuted()) {
					return;
				}
				if ((t != null) && (ess.getUser(t).isVanished())) {
					p.sendMessage(this.plugin.u.color(this.plugin.getConfig().getString("msgerr")));
					return;
				}
			}
			this.plugin.u.msgsp(mes, p.getName());
			if (s.length >= 2) {
				if (t != null) {
					this.plugin.u.getUser(t).setReply(p);
					this.plugin.u.sendcmd(mes.substring(s[0].length()).replace("{&}", ""), p,
							this.plugin.u.getUser(p).getReply());
				} else {
					p.sendMessage(this.plugin.u.color(this.plugin.getConfig().getString("msgerr")));
				}
			} else {
				p.sendMessage(cmd + " <message>");
			}
		}
	}
}