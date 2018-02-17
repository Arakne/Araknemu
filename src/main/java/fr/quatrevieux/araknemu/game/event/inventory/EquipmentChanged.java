package fr.quatrevieux.araknemu.game.event.inventory;

import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;

/**
 * Event trigger when equipment is changed
 */
final public class EquipmentChanged {
    final private ItemEntry entry;
    final private int slot;

    public EquipmentChanged(ItemEntry entry, int slot) {
        this.entry = entry;
        this.slot  = slot;
    }

    public ItemEntry entry() {
        return entry;
    }

    public int slot() {
        return slot;
    }
}
