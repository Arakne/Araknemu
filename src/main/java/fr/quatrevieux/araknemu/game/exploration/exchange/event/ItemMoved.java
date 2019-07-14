package fr.quatrevieux.araknemu.game.exploration.exchange.event;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * An item has been moved on the exchange
 */
final public class ItemMoved {
    final private ItemEntry entry;
    final private int quantity;

    public ItemMoved(ItemEntry entry, int quantity) {
        this.entry = entry;
        this.quantity = quantity;
    }

    /**
     * The moved item
     */
    public ItemEntry entry() {
        return entry;
    }

    /**
     * The new quantity
     */
    public int quantity() {
        return quantity;
    }
}
