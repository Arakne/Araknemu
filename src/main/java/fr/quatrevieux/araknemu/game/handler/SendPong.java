package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.Ping;
import fr.quatrevieux.araknemu.network.game.out.Pong;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Send pong for ping request
 */
final public class SendPong implements PacketHandler<GameSession, Ping> {
    @Override
    public void handle(GameSession session, Ping packet) throws Exception {
        session.write(new Pong());
    }

    @Override
    public Class<Ping> packet() {
        return Ping.class;
    }
}
