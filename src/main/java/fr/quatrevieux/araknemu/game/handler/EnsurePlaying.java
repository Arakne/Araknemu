package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Decorate packet handler to ensure that session has an attached player
 */
final public class EnsurePlaying<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> inner;

    public EnsurePlaying(PacketHandler<GameSession, P> inner) {
        this.inner = inner;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.player() == null)  {
            throw new CloseImmediately();
        }

        inner.handle(session, packet);
    }

    @Override
    public Class<P> packet() {
        return inner.packet();
    }
}
