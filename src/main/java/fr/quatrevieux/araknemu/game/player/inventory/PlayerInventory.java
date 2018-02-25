package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.accessory.InventoryAccessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;
import fr.quatrevieux.araknemu.game.player.inventory.slot.*;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.MoveException;
import fr.quatrevieux.araknemu.game.item.type.Equipment;

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

    final private Accessories accessories;

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

        slots = new InventorySlots(dispatcher, storage);

        for (InventoryEntry entry : storage) {
            try {
                slots.get(entry.position()).uncheckedSet(entry);
            } catch (InventoryException e) {
                entry.entity().setPosition(ItemEntry.DEFAULT_POSITION);
            }
        }

        accessories = new InventoryAccessories(slots);
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

        return target.set(item, quantity);
    }

    @Override
    public InventoryEntry delete(int id) throws InventoryException {
        InventoryEntry entry = get(id);

        slots.get(entry.position()).unset();

        return storage.delete(id);
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
     * Get current player equipments
     */
    public Collection<Equipment> equipments() {
        return slots.equipments();
    }

    /**
     * Get the player accessories
     */
    public Accessories accessories() {
        return accessories;
    }

    /**
     * Dispatch an event to the inventory
     */
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    /**
     * Move the entry to a new position
     *
     * @param entry The entry to move
     * @param position The new position
     *
     * @return true if the entry change position
     *         false if the entry is detroyed (like stacking or eat)
     */
    boolean move(InventoryEntry entry, int position) throws InventoryException {
        InventorySlot target = slots.get(position);

        if (!target.check(entry.item(), entry.quantity())) {
            throw new MoveException("Cannot move to this slot");
        }

        slots.get(entry.position()).unset();
        return entry == target.set(entry);
    }
}
