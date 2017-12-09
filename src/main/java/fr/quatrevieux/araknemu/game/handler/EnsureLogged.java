package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Decorate packet handler to ensure that session is logged
 */
final public class EnsureLogged<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> handler;


    public EnsureLogged(PacketHandler<GameSession, P> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (!session.isLogged()) {
            throw new CloseImmediately();
        }

        handler.handle(session, packet);
    }

    @Override
    public Class<P> packet() {
        return handler.packet();
    }
}
