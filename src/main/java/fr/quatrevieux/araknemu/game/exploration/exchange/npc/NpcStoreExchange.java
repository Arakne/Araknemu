package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStore;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.NpcStoreList;

/**
 * Exchange logic for the NPC store
 *
 * @todo refactor with player store (use interface)
 */
final public class NpcStoreExchange implements Sender {
    final private ExplorationPlayer player;
    final private GameNpc seller;
    final private NpcStore store;

    public NpcStoreExchange(ExplorationPlayer player, GameNpc seller, NpcStore store) {
        this.player = player;
        this.seller = seller;
        this.store = store;
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    /**
     * The the seller npc
     */
    public GameNpc seller() {
        return seller;
    }

    /**
     * Start the exchange by sending the available items
     */
    public void start() {
        player.send(new NpcStoreList(store.available()));
    }

    /**
     * Creates the dialog box interaction
     */
    public StoreDialog dialog() {
        return new StoreDialog(this);
    }

    /**
     * Stop interaction
     */
    public void stop() {
        player.interactions().remove();
    }

    /**
     * Buy an item
     *
     * @param itemId The item template id
     * @param quantity The buy quantity
     *
     * @throws IllegalArgumentException When invalid item, quantity is given, or player doesn't have enough kamas
     */
    public void buy(int itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        if (!store.has(itemId)) {
            throw new IllegalArgumentException("Item not available");
        }

        player.inventory().removeKamas(store.price(itemId, quantity));
        store.get(itemId, quantity).forEach((item, itemQuantity) -> player.inventory().add(item, itemQuantity));
    }

    /**
     * Sell an item to the NPC
     *
     * @param itemId The inventory entry id
     * @param quantity Quantity to sell
     *
     * @throws fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException When cannot sell the item
     */
    public void sell(int itemId, int quantity) {
        final ItemEntry entry = player.inventory().get(itemId);
        final long price = store.sellPrice(entry.item(), quantity);

        entry.remove(quantity);

        if (price > 0) {
            player.inventory().addKamas(price);
        }
    }
}
