package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.world.item.Item;

/**
 * Constraint for a slot
 */
public interface SlotConstraint {
    /**
     * Check the entry
     */
    public boolean check(Item item, int quantity);
}
