package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectDeleteRequest;
import fr.quatrevieux.araknemu.network.game.out.object.ItemDeletionError;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Handle object deletion
 */
final public class RemoveObject implements PacketHandler<GameSession, ObjectDeleteRequest> {
    @Override
    public void handle(GameSession session, ObjectDeleteRequest packet) throws Exception {
        try {
            session.player().inventory()
                .get(packet.id())
                .remove(packet.quantity())
            ;
        } catch (InventoryException e) {
            throw new ErrorPacket(new ItemDeletionError(), e);
        }
    }

    @Override
    public Class<ObjectDeleteRequest> packet() {
        return ObjectDeleteRequest.class;
    }
}
