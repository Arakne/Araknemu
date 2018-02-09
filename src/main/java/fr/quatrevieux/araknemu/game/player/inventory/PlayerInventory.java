package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.slot.*;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.MoveException;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Inventory for player
 */
final public class PlayerInventory implements ItemStorage<InventoryEntry> {
    final private Player player;
    final private Dispatcher dispatcher;
    final private ItemStorage<InventoryEntry> storage;
    final private InventorySlots slots;

    public PlayerInventory(Dispatcher dispatcher, Player player, Collection<InventoryService.LoadedItem> items) {
        this.dispatcher = dispatcher;
        this.player = player;

        this.storage = new SimpleItemStorage<>(
            dispatcher,
            (id, item, quantity, position) -> InventoryEntry.create(this, id, item, quantity, position),
            items
                .stream()
                .map(item -> new InventoryEntry(this, item.entity(), item.item()))
                .collect(Collectors.toList())
        );

        slots = new InventorySlots();

        for (InventoryEntry entry : storage) {
            try {
                slots.get(entry.position()).uncheckedSet(entry);
            } catch (InventoryException e) {
                entry.entity().setPosition(ItemEntry.DEFAULT_POSITION);
            }
        }
    }

    @Override
    public InventoryEntry get(int id) throws ItemNotFoundException {
        return storage.get(id);
    }

    @Override
    public InventoryEntry add(Item item, int quantity, int position) throws InventoryException {
        InventorySlot target = slots.get(position);

        if (!target.check(item, quantity)) {
            throw new InventoryException("Cannot add this item to this slot");
        }

        InventoryEntry entry = storage.add(item, quantity, position);
        target.uncheckedSet(entry);

        return entry;
    }

    @Override
    public Iterator<InventoryEntry> iterator() {
        return storage.iterator();
    }

    /**
     * Get the player id
     */
    public int playerId() {
        return player.id();
    }

    /**
     * Dispatch an event to the inventory
     */
    void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Move the entry to a new position
     *
     * @param entry The entry to move
     * @param position The new position
     */
    void move(InventoryEntry entry, int position) throws InventoryException {
        InventorySlot target = slots.get(position);

        if (!target.check(entry.item(), entry.quantity())) {
            throw new MoveException("Cannot move to this slot");
        }

        slots.get(entry.position()).unset();
        target.set(entry);
    }
}
