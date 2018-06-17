package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.Equipment;

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
    public void check(Item item, int quantity) throws InventoryException;

    /**
     * Set an entry to the slot
     *
     * This method will be called after a {@link InventoryEntry#move(int, int)}
     */
    public InventoryEntry set(InventoryEntry entry) throws InventoryException;

    /**
     * Set a new item to the slot
     *
     * This method will be called after an {@link fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory#add(Item)}
     */
    public InventoryEntry set(Item item, int quantity) throws InventoryException;

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
