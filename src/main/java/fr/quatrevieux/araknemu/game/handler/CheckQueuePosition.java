package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Check the queue position (Af packet)
 * @todo to implements
 *
 * Empty class to not fail for not found handler
 */
final public class CheckQueuePosition implements PacketHandler<GameSession, AskQueuePosition> {
    @Override
    public void handle(GameSession session, AskQueuePosition packet) {
    }

    @Override
    public Class<AskQueuePosition> packet() {
        return AskQueuePosition.class;
    }
}
