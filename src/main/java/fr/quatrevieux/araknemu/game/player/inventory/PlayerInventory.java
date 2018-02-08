package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.SimpleItemStorage;

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
    }

    @Override
    public InventoryEntry get(int id) {
        return storage.get(id);
    }

    @Override
    public InventoryEntry add(Item item, int quantity, int position) {
        // @todo check slot
        return storage.add(item, quantity, position);
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
    void move(InventoryEntry entry, int position) {
        // @todo chack & update slots
    }
}
