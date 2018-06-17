package fr.quatrevieux.araknemu.game.player.inventory.event;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Event trigger when equipment is changed
 */
final public class EquipmentChanged {
    final private ItemEntry entry;
    final private int slot;
    final private boolean equiped;

    public EquipmentChanged(ItemEntry entry, int slot, boolean equiped) {
        this.entry   = entry;
        this.slot    = slot;
        this.equiped = equiped;
    }

    /**
     * Get the item entry
     */
    public ItemEntry entry() {
        return entry;
    }

    /**
     * Get the changed slot id
     */
    public int slot() {
        return slot;
    }

    /**
     * Does the item is equiped or not ?
     */
    public boolean equiped() {
        return equiped;
    }
}
