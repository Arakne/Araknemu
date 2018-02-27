package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

import java.util.EnumSet;
import java.util.Set;

/**
 * Check if the item type is one of the given types
 */
final public class ItemTypeSetConstraint implements SlotConstraint {
    final private Set<Type> types;

    public ItemTypeSetConstraint(Set<Type> types) {
        this.types = types;
    }

    public ItemTypeSetConstraint(Type... types) {
        this(EnumSet.of(types[0], types));
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (!types.contains(item.template().type())) {
            throw new InventoryException("Bad item type");
        }
    }
}
