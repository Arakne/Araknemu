package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Move an object from the repository
 */
final public class MoveObject implements PacketHandler<GameSession, ObjectMoveRequest> {
    @Override
    public void handle(GameSession session, ObjectMoveRequest packet) throws Exception {
        session.player()
            .inventory()
            .get(packet.id())
            .move(
                packet.position(),
                packet.quantity()
            )
        ;
    }

    @Override
    public Class<ObjectMoveRequest> packet() {
        return ObjectMoveRequest.class;
    }
}
