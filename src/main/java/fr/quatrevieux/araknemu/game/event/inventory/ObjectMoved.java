package fr.quatrevieux.araknemu.game.event.inventory;

import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;

/**
 * Event sent when an inventory entry is moved
 */
final public class ObjectMoved {
    final private ItemEntry entry;

    public ObjectMoved(ItemEntry entry) {
        this.entry = entry;
    }

    public ItemEntry entry() {
        return entry;
    }
}
