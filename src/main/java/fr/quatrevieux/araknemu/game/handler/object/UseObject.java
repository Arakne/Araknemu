package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Use an object
 */
final public class UseObject implements PacketHandler<GameSession, ObjectUseRequest> {
    @Override
    public void handle(GameSession session, ObjectUseRequest packet) throws Exception {
        InventoryEntry entry = session.player().inventory().get(packet.objectId());

        UsableItem item = UsableItem.class.cast(entry.item());

        boolean result = true;
        try {
            result = packet.isTarget()
                ? handleForTarget(session, item, packet)
                : handleForSelf(session, item)
            ;
        } finally {
            if (result) {
                entry.remove(1);
            } else {
                session.write(new Noop());
            }
        }
    }

    @Override
    public Class<ObjectUseRequest> packet() {
        return ObjectUseRequest.class;
    }

    private boolean handleForSelf(GameSession session, UsableItem item) {
        if (!item.check(session.exploration())) {
            return false;
        }

        item.apply(session.exploration());
        return true;
    }

    private boolean handleForTarget(GameSession session, UsableItem item, ObjectUseRequest packet) {
        ExplorationPlayer target = session.exploration().map().getPlayer(packet.target());

        if (!item.checkTarget(session.exploration(), target, packet.cell())) {
            return false;
        }

        item.applyToTarget(session.exploration(), target, packet.cell());
        return true;
    }
}
