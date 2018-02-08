package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;

/**
 * Send the item quantity
 */
final public class ItemQuantity {
    final private ItemEntry entry;

    public ItemQuantity(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "OQ" + entry.id() + "|" + entry.quantity();
    }
}
