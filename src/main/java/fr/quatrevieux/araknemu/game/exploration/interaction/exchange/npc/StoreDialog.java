package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcStoreExchange;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemBought;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemSold;

/**
 * Dialog box interaction for a store
 *
 * @todo refactor with player store
 */
final public class StoreDialog implements ExchangeInteraction {
    final private NpcStoreExchange exchange;

    public StoreDialog(NpcStoreExchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public void leave() {
        exchange.stop();
        exchange.send(ExchangeLeaved.accepted());
    }

    @Override
    public Interaction start() {
        exchange.send(new ExchangeCreated(ExchangeType.NPC_STORE, exchange.seller()));
        exchange.start();

        return this;
    }

    @Override
    public void stop() {
        leave();
    }

    /**
     * Buy the item
     *
     * @param itemId The item id (item template id for the npc store)
     * @param quantity The asked quantity
     */
    public void buy(int itemId, int quantity) {
        try {
            exchange.buy(itemId, quantity);
            exchange.send(ItemBought.success());
        } catch (IllegalArgumentException e) {
            exchange.send(ItemBought.failed());
        }
    }

    /**
     * Sell the item
     *
     * @param itemId The inventory item entry id
     * @param quantity The sell quantity
     */
    public void sell(int itemId, int quantity) {
        try {
            exchange.sell(itemId, quantity);
            exchange.send(ItemSold.success());
        } catch (InventoryException e) {
            exchange.send(ItemSold.failed());
        }
    }
}
