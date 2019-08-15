package fr.quatrevieux.araknemu.game.item.inventory.event;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Event sent when an object is removed from an {@link fr.quatrevieux.araknemu.game.item.inventory.ItemStorage}
 *
 * @see fr.quatrevieux.araknemu.game.item.inventory.ItemStorage#delete(int)
 */
final public class ObjectDeleted {
    final private ItemEntry entry;

    public ObjectDeleted(ItemEntry entry) {
        this.entry = entry;
    }

    public ItemEntry entry() {
        return entry;
    }
}
