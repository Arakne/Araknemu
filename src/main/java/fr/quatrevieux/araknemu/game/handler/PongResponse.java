package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.PingResponse;

/**
 * Handle rpong packet {@link GameSession#isAlive()}
 */
final public class PongResponse implements PacketHandler<GameSession, PingResponse> {
    @Override
    public void handle(GameSession session, PingResponse packet) {
        //session.onPingResponse(packet);
        // @todo
    }

    @Override
    public Class<PingResponse> packet() {
        return PingResponse.class;
    }
}
