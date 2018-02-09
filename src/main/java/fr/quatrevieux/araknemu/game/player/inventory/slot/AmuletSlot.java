package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for amulets
 */
final public class AmuletSlot extends AbstractWearableSlot {
    public AmuletSlot() {
        super(0, Type.AMULETTE);
    }
}
