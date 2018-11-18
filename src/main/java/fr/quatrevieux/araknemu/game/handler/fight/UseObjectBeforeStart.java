package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Use an object before start the fight
 */
final public class UseObjectBeforeStart implements PacketHandler<GameSession, ObjectUseRequest> {
    @Override
    public void handle(GameSession session, ObjectUseRequest packet) {
        InventoryEntry entry = session.player().inventory().get(packet.objectId());

        UsableItem item = UsableItem.class.cast(entry.item());

        if (!item.checkFighter(session.fighter())) {
            session.write(new Noop());
            return;
        }

        try {
            item.applyToFighter(session.fighter());
        } finally {
            entry.remove(1);
        }
    }

    @Override
    public Class<ObjectUseRequest> packet() {
        return ObjectUseRequest.class;
    }
}
