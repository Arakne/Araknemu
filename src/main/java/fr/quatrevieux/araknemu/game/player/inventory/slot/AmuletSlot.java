package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for amulets
 */
final public class AmuletSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 0;

    public AmuletSlot(Dispatcher dispatcher) {
        super(dispatcher, SLOT_ID, Type.AMULETTE);
    }
}
