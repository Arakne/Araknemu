package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;

/**
 * Slot for amulets
 */
final public class AmuletSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 0;

    public AmuletSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage) {
        super(dispatcher, storage, SLOT_ID, Type.AMULETTE);
    }
}
