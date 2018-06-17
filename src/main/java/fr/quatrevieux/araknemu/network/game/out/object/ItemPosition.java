package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Set the item position
 */
final public class ItemPosition {
    final private ItemEntry entry;

    public ItemPosition(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "OM" + entry.id() + "|" + entry.position();
    }
}
