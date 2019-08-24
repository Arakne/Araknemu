package fr.quatrevieux.araknemu.game.handler.exchange.store;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.store.SellRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Handle selling an item to an NPC store
 */
final public class SellItem implements PacketHandler<GameSession, SellRequest> {
    @Override
    public void handle(GameSession session, SellRequest packet) {
        session.exploration().interactions().get(StoreDialog.class).sell(packet.itemId(), packet.quantity());
    }

    @Override
    public Class<SellRequest> packet() {
        return SellRequest.class;
    }
}
