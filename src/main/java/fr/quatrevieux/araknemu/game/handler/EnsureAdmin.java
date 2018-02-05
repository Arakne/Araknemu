package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Wrap packet handler for ensure that as admin access
 *
 * @param <P> The packet to handle
 */
final public class EnsureAdmin<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> inner;

    public EnsureAdmin(PacketHandler<GameSession, P> inner) {
        this.inner = inner;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.player() == null || !session.account().isMaster()) {
            throw new CloseImmediately("Admin account required");
        }

        inner.handle(session, packet);
    }

    @Override
    public Class<P> packet() {
        return inner.packet();
    }
}
