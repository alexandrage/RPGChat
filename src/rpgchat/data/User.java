package rpgchat.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class User {
	private String reply;
	private boolean glow;
	private long time;

	public void setReply(Player p) {
		this.reply = p.getName();
	}

	public Player getReply() {
		if (this.reply == null) {
			return null;
		}
		return Bukkit.getPlayerExact(this.reply);
	}

	public boolean getGlow() {
		return this.glow;
	}

	public void setGlow(boolean b) {
		this.glow = b;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}
}