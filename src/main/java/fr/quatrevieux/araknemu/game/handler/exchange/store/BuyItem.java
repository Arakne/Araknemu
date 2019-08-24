package fr.quatrevieux.araknemu.game.handler.exchange.store;

import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.store.BuyRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Handle buying an item from a store
 */
final public class BuyItem implements PacketHandler<GameSession, BuyRequest> {
    @Override
    public void handle(GameSession session, BuyRequest packet) {
        session.exploration().interactions().get(StoreDialog.class).buy(packet.itemId(), packet.quantity());
    }

    @Override
    public Class<BuyRequest> packet() {
        return BuyRequest.class;
    }
}
