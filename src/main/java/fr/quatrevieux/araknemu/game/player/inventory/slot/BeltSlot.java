package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;

/**
 * Slot for belt
 */
final public class BeltSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 3;

    public BeltSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage) {
        super(dispatcher, storage, SLOT_ID, Type.CEINTURE);
    }
}
