package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.MoveException;
import fr.quatrevieux.araknemu.game.world.item.type.Equipment;

/**
 * Slot for player inventory
 */
public interface InventorySlot {
    /**
     * The slot id
     */
    public int id();

    /**
     * Get the current entry
     */
    public InventoryEntry entry();

    /**
     * Check if the item can be moved to this slot
     *
     * @param item Item to set
     * @param quantity Quantity to set
     */
    public boolean check(Item item, int quantity);

    /**
     * Set an entry to the slot
     */
    default public void set(InventoryEntry entry) throws InventoryException {
        if (!check(entry.item(), entry.quantity())) {
            throw new MoveException("Cannot move to this slot");
        }

        uncheckedSet(entry);
    }

    /**
     * Set the entry to the slot without do any checks
     */
    public void uncheckedSet(InventoryEntry entry);

    /**
     * Remove the current entry
     */
    default public void unset() {
        uncheckedSet(null);
    }

    /**
     * Check if the slot has an equipment set
     */
    public boolean hasEquipment();

    /**
     * Get the current equipment
     */
    default public Equipment equipment() {
        return Equipment.class.cast(entry().item());
    }
}
