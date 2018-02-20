package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;

/**
 * Slot for rings
 */
final public class RingSlot extends AbstractWearableSlot {
    final static public int RING1 = 2;
    final static public int RING2 = 4;

    public RingSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, int id) {
        super(dispatcher, storage, id, Type.ANNEAU);
    }
}
