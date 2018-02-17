package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for rings
 */
final public class RingSlot extends AbstractWearableSlot {
    final static public int RING1 = 2;
    final static public int RING2 = 4;

    public RingSlot(Dispatcher dispatcher, int id) {
        super(dispatcher, id, Type.ANNEAU);
    }
}
