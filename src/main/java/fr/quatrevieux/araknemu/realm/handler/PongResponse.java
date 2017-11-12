package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.PingResponse;
import fr.quatrevieux.araknemu.network.realm.RealmSession;

/**
 * Handle rpong packet {@link RealmSession#isAlive()}
 */
final public class PongResponse implements PacketHandler<RealmSession, PingResponse> {
    @Override
    public void handle(RealmSession session, PingResponse packet) {
        session.onPingResponse(packet);
    }

    @Override
    public Class<PingResponse> packet() {
        return PingResponse.class;
    }
}
