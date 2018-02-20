package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.StackableItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

/**
 * Slot for default position
 *
 * This slot will handle item stacking
 */
final public class DefaultSlot implements InventorySlot {
    final private StackableItemStorage<InventoryEntry> storage;

    public DefaultSlot(ItemStorage<InventoryEntry> storage) {
        this.storage = new StackableItemStorage<>(storage, id());
    }

    @Override
    public int id() {
        return ItemEntry.DEFAULT_POSITION;
    }

    @Override
    public InventoryEntry entry() {
        return null;
    }

    @Override
    public boolean check(Item item, int quantity) {
        return true;
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        InventoryEntry last = storage.find(entry.item());

        if (last != null) {
            storage.delete(entry);
            last.add(entry.quantity());

            return last;
        }

        // Indexing the entry after move
        // For ensure that new item will be stacked
        storage.indexing(entry);

        return entry;
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        return storage.add(item, quantity, id());
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {}

    @Override
    public boolean hasEquipment() {
        return false;
    }
}
