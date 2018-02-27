package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

/**
 * Check the item type
 */
final public class ItemTypeConstraint implements SlotConstraint {
    final private Type type;

    public ItemTypeConstraint(Type type) {
        this.type = type;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (item.template().type() != type) {
            throw new InventoryException("Bad item type");
        }
    }
}
