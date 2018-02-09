package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.world.item.Type;

/**
 * Slot for dofus
 */
final public class DofusSlot extends AbstractWearableSlot {
    final static public int[] SLOT_IDS = new int[] {9, 10, 11, 12, 13, 14};

    public DofusSlot(int id) {
        super(id, Type.DOFUS);
    }
}
