package fr.quatrevieux.araknemu.game.player.inventory.event;

import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;

/**
 * An object is added to the inventory
 */
final public class ObjectAdded {
    final private ItemEntry entry;

    public ObjectAdded(ItemEntry entry) {
        this.entry = entry;
    }

    public ItemEntry entry() {
        return entry;
    }
}
