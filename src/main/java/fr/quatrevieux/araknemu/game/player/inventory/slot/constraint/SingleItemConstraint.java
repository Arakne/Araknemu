package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;

/**
 * Check that only one item is set to the entry
 */
final public class SingleItemConstraint implements SlotConstraint {
    @Override
    public boolean check(Item item, int quantity) {
        return quantity == 1;
    }
}
