package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.MoveException;

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
    public InventoryEntry entry() {
        return entry;
    }

    @Override
    public boolean check(Item item, int quantity) {
        if (entry != null) {
            return false;
        }

        for (SlotConstraint constraint : constraints) {
            if (!constraint.check(item, quantity)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        if (!check(entry.item(), entry.quantity())) {
            throw new MoveException("Cannot move to this slot");
        }

        uncheckedSet(entry);

        return entry;
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        if (!check(item, quantity)) {
            throw new InventoryException("Cannot set to this slot");
        }

        InventoryEntry entry = storage.add(item, quantity, id);

        uncheckedSet(entry);

        return entry;
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {
        this.entry = entry;
    }

    @Override
    public boolean hasEquipment() {
        return false;
    }
}
