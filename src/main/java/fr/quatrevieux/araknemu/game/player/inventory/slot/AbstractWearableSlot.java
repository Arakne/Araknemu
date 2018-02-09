package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemClassConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemTypeConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SingleItemConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.type.Wearable;

/**
 * Base slot class for wearable
 */
abstract public class AbstractWearableSlot implements InventorySlot {
    final private InventorySlot slot;

    public AbstractWearableSlot(int id, Type type) {
        slot = new SimpleSlot(id, new SlotConstraint[] {
            new SingleItemConstraint(),
            new ItemClassConstraint(Wearable.class),
            new ItemTypeConstraint(type)
        });
    }

    @Override
    public int id() {
        return slot.id();
    }

    @Override
    public InventoryEntry entry() {
        return slot.entry();
    }

    @Override
    public void set(InventoryEntry entry) throws InventoryException {
        slot.set(entry);
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {
        slot.uncheckedSet(entry);
    }

    @Override
    public boolean check(Item item, int quantity) {
        return slot.check(item, quantity);
    }
}
