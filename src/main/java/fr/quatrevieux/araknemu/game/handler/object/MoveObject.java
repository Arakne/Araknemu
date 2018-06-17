package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.game.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectMoveRequest;
import fr.quatrevieux.araknemu.network.game.out.object.AddItemError;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Move an object from the repository
 */
final public class MoveObject implements PacketHandler<GameSession, ObjectMoveRequest> {
    @Override
    public void handle(GameSession session, ObjectMoveRequest packet) throws Exception {
        try {
            session.player()
                .inventory()
                .get(packet.id())
                .move(
                    packet.position(),
                    packet.quantity()
                )
            ;
        } catch (BadLevelException e) {
            session.write(new AddItemError(AddItemError.Error.TOO_LOW_LEVEL));
        }
    }

    @Override
    public Class<ObjectMoveRequest> packet() {
        return ObjectMoveRequest.class;
    }
}
