package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

/**
 * Slot for helmet
 */
final public class HelmetSlot extends AbstractWearableSlot {
    final static public int SLOT_ID = 6;

    public HelmetSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner) {
        super(dispatcher, storage, owner, SLOT_ID, SuperType.HELMET);
    }
}
