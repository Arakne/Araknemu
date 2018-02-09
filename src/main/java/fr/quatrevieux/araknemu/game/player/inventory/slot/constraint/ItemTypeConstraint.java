package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Check the item type
 */
final public class ItemTypeConstraint implements SlotConstraint {
    final private Type type;

    public ItemTypeConstraint(Type type) {
        this.type = type;
    }

    @Override
    public boolean check(Item item, int quantity) {
        return item.template().type() == type;
    }
}
