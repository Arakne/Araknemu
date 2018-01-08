package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Cancel the current game action
 */
final public class CancelGameAction implements PacketHandler<GameSession, GameActionCancel> {
    @Override
    public void handle(GameSession session, GameActionCancel packet) throws Exception {
        session
            .exploration()
            .actionQueue()
            .cancel(
                packet.actionId(),
                packet.argument()
            )
        ;
    }

    @Override
    public Class<GameActionCancel> packet() {
        return GameActionCancel.class;
    }
}
