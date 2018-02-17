package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for helmet
 */
final public class HelmetSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 6;

    public HelmetSlot(Dispatcher dispatcher) {
        super(dispatcher, SLOT_ID, Type.COIFFE);
    }
}
