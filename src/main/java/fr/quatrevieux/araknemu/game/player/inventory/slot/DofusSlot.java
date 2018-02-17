package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for dofus
 */
final public class DofusSlot extends AbstractWearableSlot {
    final static public int[] SLOT_IDS = new int[] {9, 10, 11, 12, 13, 14};

    public DofusSlot(Dispatcher dispatcher, int id) {
        super(dispatcher, id, Type.DOFUS);
    }
}
