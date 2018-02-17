package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import fr.quatrevieux.araknemu.game.world.item.Item;

/**
 * Base slot class
 */
final public class SimpleSlot implements InventorySlot {
    final private int id;
    final private SlotConstraint[] constraints;

    private InventoryEntry entry;

    public SimpleSlot(int id, SlotConstraint[] constraints) {
        this.id = id;
        this.constraints = constraints;
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
    public void uncheckedSet(InventoryEntry entry) {
        this.entry = entry;
    }

    @Override
    public boolean hasEquipment() {
        return false;
    }
}
