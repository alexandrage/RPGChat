package rpgchat.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import rpgchat.Main;
import rpgchat.packet.WrapperPlayServerEntityMetadata;

public class PacketMeta extends PacketAdapter {
	private Main main;

	public PacketMeta(Main main, PacketType[] type) {
		super(main, type);
		this.main = main;
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		try {
			WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event.getPacket());
			Entity e = packet.getEntity(event);
			if ((e.getType() == EntityType.PLAYER) && (this.main.u.getUser((Player) e).getGlow())) {
				for (WrappedWatchableObject meta : packet.getMetadata()) {
					if (meta.getIndex() == 0) {
						byte a = 64;
						byte b = ((Byte) meta.getValue()).byteValue();
						if ((b != 0) && (b < 64)) {
							byte c = (byte) (a + b);
							meta.setValue(Byte.valueOf(c));
						}
						if (b == 0) {
							meta.setValue(Byte.valueOf(a));
						}
					}
				}
			}
		} catch (Exception localException) {
		}
	}
}
