package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemClassConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;

import java.util.Optional;

/**
 * Slot for usable items
 */
final public class UsableSlot implements InventorySlot {
    final static public int SLOT_ID_START = 35;
    final static public int SLOT_ID_END   = 57;

    final private InventorySlot slot;

    public UsableSlot(ItemStorage<InventoryEntry> storage, int id) {
        slot = new SimpleSlot(
            id,
            new SlotConstraint[] {
                new ItemClassConstraint(UsableItem.class)
            },
            storage
        );
    }

    @Override
    public int id() {
        return slot.id();
    }

    @Override
    public Optional<InventoryEntry> entry() {
        return slot.entry();
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        slot.check(item, quantity);
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        return slot.set(entry);
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        return slot.set(item, quantity);
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {
        slot.uncheckedSet(entry);
    }
}
