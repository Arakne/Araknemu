package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

/**
 * Check that only one item is set to the entry
 */
final public class SingleItemConstraint implements SlotConstraint {
    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (quantity != 1) {
            throw new InventoryException("Invalid quantity");
        }
    }
}
