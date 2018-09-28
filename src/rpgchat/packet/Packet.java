package rpgchat.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import rpgchat.Main;

public class Packet {
	public void hack(Main plugin) {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketChat(plugin, new PacketType[] { PacketType.Play.Client.CHAT }));
	}
}