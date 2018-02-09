package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;

/**
 * Check for the item class
 */
final public class ItemClassConstraint implements SlotConstraint {
    final private Class<? extends Item> type;

    public ItemClassConstraint(Class<? extends Item> type) {
        this.type = type;
    }

    @Override
    public boolean check(Item item, int quantity) {
        return type.isInstance(item);
    }
}
