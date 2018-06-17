package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

/**
 * Check the item type
 */
final public class ItemTypeConstraint implements SlotConstraint {
    final private SuperType type;

    public ItemTypeConstraint(SuperType type) {
        this.type = type;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (item.type().superType() != type) {
            throw new InventoryException("Bad item type");
        }
    }
}
