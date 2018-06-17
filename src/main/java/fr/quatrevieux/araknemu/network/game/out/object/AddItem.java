package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Add an item to the inventory
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L107
 */
final public class AddItem {
    final private ItemEntry entry;

    public AddItem(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "OAKO" + new ItemSerializer(entry);
    }
}
