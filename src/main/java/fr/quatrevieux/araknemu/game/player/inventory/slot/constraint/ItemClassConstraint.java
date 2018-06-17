package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

/**
 * Check for the item class
 */
final public class ItemClassConstraint implements SlotConstraint {
    final private Class<? extends Item> type;

    public ItemClassConstraint(Class<? extends Item> type) {
        this.type = type;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (!type.isInstance(item)) {
            throw new InventoryException("Bad item class");
        }
    }
}
