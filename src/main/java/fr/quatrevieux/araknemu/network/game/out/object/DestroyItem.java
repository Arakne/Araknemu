package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Remove the item from the inventory
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L191
 */
final public class DestroyItem {
    final private ItemEntry entry;

    public DestroyItem(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "OR" + entry.id();
    }
}
