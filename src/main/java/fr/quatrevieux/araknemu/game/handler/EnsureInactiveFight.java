package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Ensure that the session contains is not in fight, or the fight is not active
 *
 * @param <P> Packet to handler
 */
final public class EnsureInactiveFight<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> handler;

    public EnsureInactiveFight(PacketHandler<GameSession, P> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.fighter() != null && session.fighter().fight() != null && session.fighter().fight().active()) {
            throw new ErrorPacket(Error.cantDoDuringFight());
        }

        handler.handle(session, packet);
    }

    @Override
    public Class<P> packet() {
        return handler.packet();
    }
}
