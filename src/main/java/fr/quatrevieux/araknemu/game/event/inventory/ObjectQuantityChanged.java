package fr.quatrevieux.araknemu.game.event.inventory;

import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;

/**
 * The object quantity is changed
 */
final public class ObjectQuantityChanged {
    final private ItemEntry entry;

    public ObjectQuantityChanged(ItemEntry entry) {
        this.entry = entry;
    }

    public ItemEntry entry() {
        return entry;
    }
}
