package rpgchat.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import rpgchat.Main;

public class PacketChat extends PacketAdapter {
	private Main main;

	public PacketChat(Main main, PacketType[] type) {
		super(main, type);
		this.main = main;
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		if (this.main.u.check(event.getPlayer(), 500L)) {
			event.setCancelled(true);
		}
	}
}