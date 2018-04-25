package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Fight game action acknowledge
 */
final public class TerminateTurnAction implements PacketHandler<GameSession, GameActionAcknowledge> {
    @Override
    public void handle(GameSession session, GameActionAcknowledge packet) {
        session.fighter().turn().terminate();
    }

    @Override
    public Class<GameActionAcknowledge> packet() {
        return GameActionAcknowledge.class;
    }
}
