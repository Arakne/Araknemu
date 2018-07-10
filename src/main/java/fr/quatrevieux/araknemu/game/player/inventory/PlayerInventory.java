package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.item.type.Equipment;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.accessory.InventoryAccessories;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.ItemSets;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Inventory for player
 */
final public class PlayerInventory implements ItemStorage<InventoryEntry>, Dispatcher {
    final private ItemStorage<InventoryEntry> storage;
    final private InventorySlots slots;
    final private Accessories accessories;
    final private ItemSets itemSets;

    private GamePlayer owner;

    public PlayerInventory(Collection<InventoryService.LoadedItem> items) {
        this.storage = new SimpleItemStorage<>(
            this,
            (id, item, quantity, position) -> InventoryEntry.create(this, id, item, quantity, position),
            items
                .stream()
                .map(item -> new InventoryEntry(this, item.entity(), item.item()))
                .collect(Collectors.toList())
        );

        slots = new InventorySlots(this, storage);
        accessories = new InventoryAccessories(slots);
        itemSets = new ItemSets(this);
    }

    @Override
    public InventoryEntry get(int id) throws ItemNotFoundException {
        return storage.get(id);
    }

    @Override
    public InventoryEntry add(Item item, int quantity, int position) throws InventoryException {
        InventorySlot target = slots.get(position);

        target.check(item, quantity);

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

    @Override
    public void dispatch(Object event) {
        owner.dispatch(event);
    }

    /**
     * Get the inventory owner
     */
    public GamePlayer owner() {
        return owner;
    }

    /**
     * Attach the inventory to its owner
     */
    public PlayerInventory attach(GamePlayer owner) {
        if (this.owner != null) {
            throw new IllegalStateException("Owner already set");
        }

        this.owner = owner;
        slots.init(owner);

        return this;
    }

    /**
     * Get current player equipments
     */
    public Collection<Equipment> equipments() {
        return slots.equipments();
    }

    /**
     * Get an item by the slot id
     *
     * @param slotId The slot of the item
     *
     * @return The item contained in the slot, or an empty Optional
     */
    public Optional<Item> bySlot(int slotId) throws InventoryException {
        return slots.get(slotId).entry().map(InventoryEntry::item);
    }

    /**
     * Get the player accessories
     */
    public Accessories accessories() {
        return accessories;
    }

    /**
     * Get the player item sets
     */
    public ItemSets itemSets() {
        return itemSets;
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

        target.check(entry.item(), entry.quantity());

        slots.get(entry.position()).unset();
        return entry == target.set(entry);
    }
}
