package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

/**
 * Constraint for a slot
 */
public interface SlotConstraint {
    /**
     * Check the entry
     */
    public void check(Item item, int quantity) throws InventoryException;
}
