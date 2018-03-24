package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * End the current game action with success
 */
final public class EndGameAction implements PacketHandler<GameSession, GameActionAcknowledge> {
    @Override
    public void handle(GameSession session, GameActionAcknowledge packet) throws Exception {
        session
            .exploration()
            .interactions()
            .end(packet.actionId())
        ;
    }

    @Override
    public Class<GameActionAcknowledge> packet() {
        return GameActionAcknowledge.class;
    }
}
