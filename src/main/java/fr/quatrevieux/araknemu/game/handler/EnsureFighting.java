package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.exception.CloseSession;
import fr.quatrevieux.araknemu.network.exception.WritePacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.slf4j.LoggerFactory;

/**
 * Ensure that the session contains a valid fighter
 *
 * @param <P> Packet to handler
 */
final public class EnsureFighting<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> handler;

    public EnsureFighting(PacketHandler<GameSession, P> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.fighter() == null) {
            throw new CloseImmediately("Not in fight");
        }

        session.fighter().fight().execute(
            () -> {
                try {
                    handler.handle(session, packet);
                    // @todo call session exception handler
                } catch (Exception e) {
                    Throwable cause = e;

                    if (e instanceof WritePacket) {
                        session.write(WritePacket.class.cast(e).packet());
                        cause = e.getCause();
                    }

                    if (e instanceof CloseSession) {
                        session.close();
                    }

                    if (cause != null) {
                        LoggerFactory.getLogger(GameService.class).error("Error during handle packet " + packet.getClass().getSimpleName(), cause);
                    }
                }
            }
        );
    }

    @Override
    public Class<P> packet() {
        return handler.packet();
    }
}
