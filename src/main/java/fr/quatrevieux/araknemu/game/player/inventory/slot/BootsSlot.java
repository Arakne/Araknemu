package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for boots
 */
final public class BootsSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 5;

    public BootsSlot(Dispatcher dispatcher) {
        super(dispatcher, SLOT_ID, Type.BOTTES);
    }
}
