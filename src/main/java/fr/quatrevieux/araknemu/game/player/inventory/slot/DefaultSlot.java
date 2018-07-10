package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.StackableItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

import java.util.Optional;

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
    public Optional<InventoryEntry> entry() {
        return Optional.empty();
    }

    @Override
    public void check(Item item, int quantity) {}

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        return storage.find(entry.item())
            .map(last -> {
                storage.delete(entry);
                last.add(entry.quantity());

                return last;
            })
            .orElseGet(() -> {
                // Indexing the entry after move
                // For ensure that new item will be stacked
                storage.indexing(entry);

                return entry;
            })
        ;
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        return storage.add(item, quantity, id());
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {}
}
