package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

import static org.junit.jupiter.api.Assertions.fail;

public class LoaderTestCase extends GameBaseCase {
    public void assertHandlePacket(Class<? extends Packet> type, PacketHandler[] handlers) {
        for (PacketHandler handle : handlers) {
            if (handle.packet().equals(type)) {
                return;
            }
        }

        fail("No handle found for packet " + type.getName());
    }
}
