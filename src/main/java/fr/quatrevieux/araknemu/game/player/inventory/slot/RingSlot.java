package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

/**
 * Slot for rings
 */
final public class RingSlot extends AbstractWearableSlot {
    final static public int RING1 = 2;
    final static public int RING2 = 4;

    public RingSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner, int id) {
        super(dispatcher, storage, owner, id, SuperType.RING);
    }
}
