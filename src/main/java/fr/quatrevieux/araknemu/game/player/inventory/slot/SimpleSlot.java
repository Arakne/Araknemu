package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.item.type.Equipment;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

import java.util.Optional;

/**
 * Base slot class
 */
final public class SimpleSlot implements InventorySlot {
    final private int id;
    final private SlotConstraint[] constraints;
    final private ItemStorage<InventoryEntry> storage;

    private InventoryEntry entry;

    public SimpleSlot(int id, SlotConstraint[] constraints, ItemStorage<InventoryEntry> storage) {
        this.id = id;
        this.constraints = constraints;
        this.storage = storage;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Optional<InventoryEntry> entry() {
        return Optional.ofNullable(entry);
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (entry != null) {
            throw new InventoryException("Slot is full");
        }

        for (SlotConstraint constraint : constraints) {
            constraint.check(item, quantity);
        }
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        check(entry.item(), entry.quantity());

        uncheckedSet(entry);

        return entry;
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        check(item, quantity);

        InventoryEntry entry = storage.add(item, quantity, id);

        uncheckedSet(entry);

        return entry;
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {
        this.entry = entry;
    }
}
