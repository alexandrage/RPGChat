package rpgchat.utils;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import rpgchat.Main;
import rpgchat.data.User;

public class Utils {
	private static Pattern patern;
	static {
		patern = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-OR]");
	}

	private static Pattern paternHex;
	static {
		paternHex = Pattern.compile("[0-9a-fA-F]");
	}

	private static String colorHex(String value) {
		StringBuilder sb = new StringBuilder();
		char[] b = value.toCharArray();
		for (int i = 0; i < b.length; i++) {
			if (b[i] == '§' && i + 7 < value.length() && b[i + 1] == '#') {
				StringBuilder tmp = new StringBuilder();
				tmp.append("§x");
				int z = 7;
				for (int x = 0; x < 6; x++) {
					if (paternHex.matcher(String.valueOf(b[i + x + 2])).matches()) {
						tmp.append("§" + b[i + x + 2]);
					} else {
						tmp.setLength(0);
						z = 0;
						break;
					}
				}
				i += z;
				sb.append(tmp);
				tmp.setLength(0);
			} else {
				sb.append(b[i]);
			}
		}
		value = sb.toString();
		sb.setLength(0);
		return value.replace("§#", "#");
	}

	public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; ++i) {
			if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr#".indexOf(b[i + 1]) > -1) {
				b[i] = '§';
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return colorHex(new String(b));
	}

	private Main plugin;

	public Utils(Main plugin) {
		this.plugin = plugin;
	}

	private Map<String, User> user = new ConcurrentHashMap<String, User>();

	public void setUser(Player p) {
		user.put(p.getName().toLowerCase(), new User());
	}

	public User getUser(Player p) {
		User us = user.get(p.getName().toLowerCase());
		if (us == null) {
			setUser(p);
			us = user.get(p.getName().toLowerCase());
		}
		return us;
	}

	public void removeUser(Player p) {
		user.remove(p.getName().toLowerCase());
	}

	public void removeAll() {
		user.clear();
	}

	public boolean check(Player p, long tmp) {
		long time = System.currentTimeMillis();
		User us = this.plugin.u.getUser(p);
		long last = us.getTime();
		if ((time - last) < tmp) {
			return true;
		}
		us.setTime(time);
		return false;
	}

	public boolean e(String cmd, String... s) {
		for (String c : s) {
			if (cmd.equalsIgnoreCase(c)) {
				return true;
			}
		}
		for (String c : s) {
			if (cmd.equalsIgnoreCase("/essentials:" + c.substring(1))) {
				return true;
			}
		}
		for (String c : s) {
			if (cmd.equalsIgnoreCase("/" + this.plugin.getName().toLowerCase() + ":" + c.substring(1))) {
				return true;
			}
		}
		return false;
	}

	public String color(String s) {
		return translateAlternateColorCodes('&', s);
	}

	public void msgsp(String message, String name) {
		if (this.plugin.ess != null) {
			for (Player ps : Bukkit.getOnlinePlayers()) {
				if (ps.getName().equalsIgnoreCase(name)) {
					continue;
				}
				boolean sp = false;
				if (this.plugin.ess != null) {
					sp = EssUtils.isSocialSpyEnabled(this.plugin.ess, ps);
				}
				if (sp) {
					ps.sendMessage(name + ": " + message);
				}
			}
		}
	}

	public void sendJoin(Player p) {
		if (p.hasPermission("chat.join")) {
			String world = p.getWorld().getName();
			String[] group = this.plugin.chat.getPlayerGroups(p);
			String prefix = this.plugin.chat.getPlayerPrefix(p);
			String name = "{&}" + p.getName() + "{&}";
			String suffix = this.plugin.chat.getPlayerSuffix(p);
			String format = this.plugin.getConfig().getString("formatjoin",
					"{prefix}{player}{suffix}&r Зашел на сервер.");
			format = replace(format, world, group[0], prefix, name, suffix);
			BaseComponent[] bmsg = generade(format, name);
			for (Player ps : Bukkit.getOnlinePlayers()) {
				ps.spigot().sendMessage(bmsg);
			}
		}
	}

	public void sendQuit(Player p) {
		if (p.hasPermission("chat.quit")) {
			String world = p.getWorld().getName();
			String[] group = this.plugin.chat.getPlayerGroups(p);
			String prefix = this.plugin.chat.getPlayerPrefix(p);
			String name = "{&}" + p.getName() + "{&}";
			String suffix = this.plugin.chat.getPlayerSuffix(p);
			String format = this.plugin.getConfig().getString("formatquit", "{prefix}{player}{suffix}&r {message}");
			format = replace(format, world, group[0], prefix, name, suffix);
			BaseComponent[] bmsg = generade(format, name);
			for (Player ps : Bukkit.getOnlinePlayers()) {
				ps.spigot().sendMessage(bmsg);
			}
		}
	}

	public String replace(String message, Player p) {
		String format = "";
		String world = p.getWorld().getName();
		String[] group = this.plugin.chat.getPlayerGroups(p);
		String prefix = this.plugin.chat.getPlayerPrefix(p);
		String name = "{&}" + p.getName() + "{&}";
		String suffix = this.plugin.chat.getPlayerSuffix(p);
		int radius = this.plugin.getConfig().getInt("chatradius");
		if (message.startsWith("!") && p.hasPermission("chat.shout") && radius > 0 && message.length() > 1) {
			format = replace(getFormat(group[0], "formatglobal"), world, group[0], prefix, name, suffix);
		} else if (message.startsWith("?") && p.hasPermission("chat.quest") && message.length() > 1) {
			format = replace(getFormat(group[0], "formatquest"), world, group[0], prefix, name, suffix);
		} else {
			format = replace(getFormat(group[0], "formatlocal"), world, group[0], prefix, name, suffix);
		}
		return format;
	}

	public void send(String message, Player p, String txt) {
		int radius = this.plugin.getConfig().getInt("chatradius");
		if (p.hasPermission("chat.color")) {
			txt = color(txt);
		}
		boolean isGlobal = false;
		boolean isMsg = false;
		if (txt.startsWith("!") && p.hasPermission("chat.shout") && radius > 0 && txt.length() > 1) {
			message = replace(message, txt.substring(1));
			isGlobal = true;
		} else if (txt.startsWith("?") && p.hasPermission("chat.quest") && txt.length() > 1) {
			message = replace(message, txt.substring(1));
			isGlobal = true;
		} else {
			message = replace(message, txt);
		}
		String name = "{&}" + p.getName() + "{&}";
		BaseComponent[] console = TextComponent.fromLegacyText(message.replace("{&}", ""));
		for (BaseComponent tmp : console) {
			String cname = tmp.getColor().getName();
			if (cname.startsWith("#")) {
				Color c = Color.decode(tmp.getColor().getName());
				ChatColor color = ColorUtil.fromRGB(c.getRed(), c.getGreen(), c.getBlue());
				tmp.setColor(net.md_5.bungee.api.ChatColor.getByChar(color.getChar()));
			}
		}
		this.plugin.getServer().getConsoleSender().spigot().sendMessage(console);
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Player ps : Bukkit.getOnlinePlayers()) {
			String n = ps.getName();
			String[] spl = message.split("\\{&\\}");
			StringBuilder sbN = new StringBuilder();
			boolean note = false;
			String color = "&r";
			for (String N : spl[2].split(" ")) {
				if (N.equalsIgnoreCase(n)) {
					N = N.replace(n, color("&e@" + n + color));
					note = true;
				} else {
					color = trim(N);
				}
				sbN.append(N).append(" ");
			}
			String mess = spl[0] + "{&}" + spl[1] + "{&}" + sbN.toString();
			sbN = null;
			BaseComponent[] bmessages = generade(mess, name, isGlobal, isMsg);
			if (this.plugin.ess != null) {
				boolean sp = EssUtils.isSocialSpyEnabled(this.plugin.ess, ps);
				boolean ignore = EssUtils.isIgnoredPlayer(this.plugin.ess, ps, p);
				if (ignore && !p.equals(sp) && !sp) {
					continue;
				}
			}
			Location loc1 = ps.getLocation();
			Location loc2 = p.getLocation();
			double dis = getDistance(loc1.getX(), loc1.getZ(), loc2.getX(), loc2.getZ());
			if (loc1.getWorld() != loc2.getWorld()) {
				dis = Double.MAX_VALUE;
			}
			if (isGlobal || radius <= 0) {
				ps.spigot().sendMessage(bmessages);
				if (note) {
					ps.playNote(ps.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
				}
			} else if ((dis < radius) || ps.hasPermission("chat.bypass")) {
				boolean isHide = false;
				if (this.plugin.ess != null) {
					isHide = EssUtils.isHidden(this.plugin.ess, ps);
				}
				if (ps.hasPermission("chat.bypass") && dis > radius) {
					isHide = true;
				}
				if (!isHide && ps != p) {
					sb.append(ps.getName());
					sb.append(" ");
					i++;
				}
				ps.spigot().sendMessage(bmessages);
				if (note) {
					ps.playNote(ps.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.A));
				}
			}
		}
		if (isGlobal) {
			return;
		}
		if (i == 0) {
			String s = color(this.plugin.getConfig().getString("countnull", "Вас никто не услышал, рядом никого нет."));
			if (s.length() > 0) {
				p.sendMessage(s);
			}
			return;
		}
		List<String> list = s(sb.toString(), 60);
		sb = null;
		String players = list.toString().substring(1, list.toString().length() - 1);
		String s = this.plugin.getConfig().getString("count", "Вас услышало: [{player}] человек.");
		if (s.length() > 0) {
			BaseComponent[] pref = generade(s, "{player}", i, players.replace(",", "\n"));
			p.spigot().sendMessage(pref);
		}
	}

	public void sendcmd(String message, Player p, Player t) {
		if (this.plugin.ess != null) {
			boolean ignore = EssUtils.isIgnoredPlayer(this.plugin.ess, t, p);
			if (ignore) {
				String msgignore = this.plugin.getConfig().getString("msgignore", "&6Ошибка: &4Игрок вас игнорирует.");
				p.sendMessage(color(msgignore));
				return;
			}
		}
		if (p.hasPermission("chat.color")) {
			message = color(message);
		}
		boolean isGlobal = false;
		boolean isMsg = false;
		String rprefix = null;
		String rname = null;
		String rsuffix = null;
		if (t != null) {
			isMsg = true;
			rprefix = this.plugin.chat.getPlayerPrefix(t);
			rname = "{&}" + t.getName() + "{&}";
			rsuffix = this.plugin.chat.getPlayerSuffix(t);
		}
		String format = "";
		String prefix = this.plugin.chat.getPlayerPrefix(p);
		String name = "{&}" + p.getName() + "{&}";
		String suffix = this.plugin.chat.getPlayerSuffix(p);
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		date.setTime(System.currentTimeMillis());
		format = color(String
				.format(this.plugin.getConfig().getString("msgformat", "[%s -> %s] "),
						new Object[] { this.plugin.getConfig().getString("msgme", "Я"), rprefix + rname + rsuffix })
				.replace("{time}", df.format(date))) + message;
		BaseComponent[] bmessages = generade(format, rname, isGlobal, isMsg);
		p.spigot().sendMessage(bmessages);
		format = color(String
				.format(this.plugin.getConfig().getString("msgformat", "[%s -> %s] "),
						new Object[] { prefix + name + suffix, this.plugin.getConfig().getString("msgme") })
				.replace("{time}", df.format(date))) + message;
		bmessages = generade(format, name, isGlobal, isMsg);
		t.spigot().sendMessage(bmessages);
	}

	private BaseComponent[] generade(String message, String name, int count, String players) {
		BaseComponent[] pref = TextComponent.fromLegacyText(color(message));
		for (int i = 0; i < pref.length; i++) {
			String s = pref[i].toLegacyText();
			if (s.contains(name)) {
				BaseComponent tmp = new TextComponent();
				BaseComponent bs1 = TextComponent.fromLegacyText(s.substring(0, s.indexOf(name)))[0];
				copyFormatting(pref[i], bs1);
				tmp.addExtra(bs1);
				BaseComponent bs2 = TextComponent.fromLegacyText(name.replace("{player}", count + ""))[0];
				copyFormatting(pref[i], bs2);
				BaseComponent[] hname = TextComponent.fromLegacyText(players);
				HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hname);
				bs2.setHoverEvent(hoverEvent);
				tmp.addExtra(bs2);
				BaseComponent bs3 = TextComponent.fromLegacyText(s.substring(s.indexOf(name) + name.length()))[0];
				copyFormatting(pref[i], bs3);
				tmp.addExtra(bs3);
				pref[i] = tmp;
			}
		}
		return pref;
	}

	private BaseComponent[] generade(String format, String name, boolean isGlobal, boolean isMsg) {
		BaseComponent[] bmessages = TextComponent.fromLegacyText(format);
		for (int i = 0; i < bmessages.length; i++) {
			String s = bmessages[i].toLegacyText();
			if (s.contains(name)) {
				TextComponent tmp = new TextComponent();
				BaseComponent bs1 = TextComponent.fromLegacyText(s.substring(0, s.indexOf(name)))[0];
				copyFormatting(bmessages[i], bs1);
				tmp.addExtra(bs1);
				BaseComponent bs2 = TextComponent.fromLegacyText(name.replace("{&}", ""))[0];
				copyFormatting(bmessages[i], bs2);
				tmp.addExtra(bs2);
				BaseComponent[] hname = TextComponent.fromLegacyText(name.replace("{&}", ""));
				HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hname);
				bs2.setHoverEvent(hoverEvent);
				String cname = name.replace("{&}", "") + " ";
				if (isGlobal) {
					cname = "!" + cname;
				}
				if (isMsg) {
					cname = "/m " + cname;
				}
				ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cname);
				bs2.setClickEvent(clickEvent);
				BaseComponent bs3 = TextComponent.fromLegacyText(s.substring(s.indexOf(name) + name.length()))[0];
				copyFormatting(bmessages[i], bs3);
				tmp.addExtra(bs3);
				bmessages[i] = tmp;
				break;
			}
		}
		return bmessages;
	}

	private BaseComponent[] generade(String format, String name) {
		BaseComponent[] bmessages = TextComponent.fromLegacyText(format);
		for (int i = 0; i < bmessages.length; i++) {
			String s = bmessages[i].toLegacyText();
			if (s.contains(name)) {
				TextComponent tmp = new TextComponent();
				BaseComponent bs1 = TextComponent.fromLegacyText(s.substring(0, s.indexOf(name)))[0];
				copyFormatting(bmessages[i], bs1);
				tmp.addExtra(bs1);
				BaseComponent bs2 = TextComponent.fromLegacyText(name.replace("{&}", ""))[0];
				copyFormatting(bmessages[i], bs2);
				tmp.addExtra(bs2);
				BaseComponent[] hname = TextComponent.fromLegacyText(name.replace("{&}", ""));
				HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hname);
				bs2.setHoverEvent(hoverEvent);
				String cname = name.replace("{&}", "") + " ";
				ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cname);
				bs2.setClickEvent(clickEvent);
				BaseComponent bs3 = TextComponent.fromLegacyText(s.substring(s.indexOf(name) + name.length()))[0];
				copyFormatting(bmessages[i], bs3);
				tmp.addExtra(bs3);
				bmessages[i] = tmp;
				break;
			}
		}
		return bmessages;
	}

	private String getFormat(String group, String format) {
		String def = this.plugin.getConfig().getString(format, "{prefix}{player}{suffix} {message}");
		return this.plugin.getConfig().getString(group + "." + format, def);
	}

	private String replace(String format, String world, String group, String prefix, String name, String suffix) {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		date.setTime(System.currentTimeMillis());
		String t = df.format(date);
		format = color(format.replace("{time}", t).replace("{world}", world).replace("{group}", group)
				.replace("{prefix}", prefix).replace("{player}", name).replace("{suffix}", suffix));
		return format;
	}

	private String replace(String format, String message) {
		format = format.replace("{message}", message);
		return format;
	}

	public String replaceList(String format, Player p) {
		String prefix = "";
		String suffix = "";
		if (this.plugin.chat != null) {
			prefix = this.plugin.chat.getPlayerPrefix(p);
			suffix = this.plugin.chat.getPlayerSuffix(p);
		}
		return color(format.replace("{prefix}", prefix).replace("{player}", p.getName()).replace("{suffix}", suffix));
	}

	private int getDistance(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public static List<String> s(String st, int spl) {
		String[] arrWords = st.split(" ");
		ArrayList<String> list = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		int length = arrWords.length;
		while (index != length) {
			if (sb.length() + arrWords[index].length() <= spl) {
				sb.append(arrWords[index]).append(" ");
				index++;
			} else {
				list.add(sb.toString());
				sb.setLength(0);
			}
		}
		if (sb.length() > 0) {
			list.add(sb.toString());
		}

		sb = null;
		return list;
	}

	private void copyFormatting(BaseComponent component, BaseComponent now) {
		now.setColor(component.getColorRaw());
		now.setBold(component.isBoldRaw());
		now.setItalic(component.isItalicRaw());
		now.setUnderlined(component.isUnderlinedRaw());
		now.setStrikethrough(component.isStrikethroughRaw());
		now.setObfuscated(component.isObfuscatedRaw());
		now.setInsertion(component.getInsertion());
		now.setClickEvent(component.getClickEvent());
		now.setHoverEvent(component.getHoverEvent());
		if (component.getExtra() != null) {
			for (BaseComponent extra : component.getExtra()) {
				now.addExtra(extra.duplicate());
			}
		}
	}

	private String trim(String string) {
		if (string.lastIndexOf('§') != -1 && string.substring(string.lastIndexOf('§')).length() > 1) {
			String tmp = string.substring(string.lastIndexOf('§'), string.lastIndexOf('§') + 2);
			if (patern.matcher(tmp).matches()) {
				return tmp;
			}
		}
		return "&r";
	}
}