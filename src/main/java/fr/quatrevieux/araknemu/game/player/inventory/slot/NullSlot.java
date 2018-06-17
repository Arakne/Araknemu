package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

/**
 * Null object for slot
 */
final public class NullSlot implements InventorySlot {
    final private int id;

    public NullSlot(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public InventoryEntry entry() {
        return null;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        throw new InventoryException("Null slot");
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {}

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        throw new InventoryException("Null slot");
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        throw new InventoryException("Null slot");
    }

    @Override
    public boolean hasEquipment() {
        return false;
    }
}
