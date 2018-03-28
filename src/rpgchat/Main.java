package rpgchat;

import net.milkbowl.vault.chat.Chat;
import rpgchat.listener.EventListener;
import rpgchat.listener.MsgListener;
import rpgchat.listener.ReplyListener;
import rpgchat.packet.Packet;
import rpgchat.team.Scheduler;
import rpgchat.utils.Utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public Chat chat = null;
	public Plugin ess = null;
	public Utils u;

	@Override
	public void onEnable() {
		u = new Utils(this);
		getCommand("msg").setExecutor(new MsgListener(this));
		getCommand("r").setExecutor(new ReplyListener());
		getConfig().options().copyDefaults(true);
		saveConfig();
		setupChat();
		reloadConfig();
		if (getConfig().getBoolean("useteam", true)) {
			new Scheduler(this).runTaskTimerAsynchronously(this, 30, 20);
		}
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		ess = getServer().getPluginManager().getPlugin("Essentials");
		new Packet().hack(this);
	}

	@Override
	public void onDisable() {
		chat = null;
		ess = null;
		this.u.removeAll();
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		if (chatProvider != null) {
			chat = (Chat) chatProvider.getProvider();
		}
		return chat != null;
	}
}