package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Switch between handler when the current player is in fight or exploring
 */
final public class ExploringOrFightingSwitcher<P extends Packet> implements PacketHandler<GameSession, P> {
    final private PacketHandler<GameSession, P> exploringHandler;
    final private PacketHandler<GameSession, P> fightingHandler;

    /**
     * ExploringOrFightingSwitcher constructor
     *
     * @param exploringHandler The handler to use when client is in exploration
     * @param fightingHandler The handler to use when client is in exploration
     */
    public ExploringOrFightingSwitcher(PacketHandler<GameSession, P> exploringHandler, PacketHandler<GameSession, P> fightingHandler) {
        this.exploringHandler = exploringHandler;
        this.fightingHandler = fightingHandler;
    }

    @Override
    public void handle(GameSession session, P packet) throws Exception {
        if (session.fighter() != null) {
            fightingHandler.handle(session, packet);
        } else if (session.exploration() != null) {
            exploringHandler.handle(session, packet);
        } else {
            throw new CloseImmediately("The player should be in exploration or fight");
        }
    }

    @Override
    public Class<P> packet() {
        return exploringHandler.packet();
    }
}
