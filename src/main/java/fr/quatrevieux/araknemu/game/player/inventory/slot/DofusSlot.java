package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;

/**
 * Slot for dofus
 */
final public class DofusSlot extends AbstractWearableSlot {
    final static public int[] SLOT_IDS = new int[] {9, 10, 11, 12, 13, 14};

    public DofusSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, int id) {
        super(dispatcher, storage, id, Type.DOFUS);
    }
}
