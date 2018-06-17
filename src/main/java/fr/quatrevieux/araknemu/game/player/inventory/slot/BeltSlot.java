package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

/**
 * Slot for belt
 */
final public class BeltSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 3;

    public BeltSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner) {
        super(dispatcher, storage, owner, SLOT_ID, SuperType.BELT);
    }
}
