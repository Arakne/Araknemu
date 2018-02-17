package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for belt
 */
final public class BeltSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 3;

    public BeltSlot(Dispatcher dispatcher) {
        super(dispatcher, SLOT_ID, Type.CEINTURE);
    }
}
