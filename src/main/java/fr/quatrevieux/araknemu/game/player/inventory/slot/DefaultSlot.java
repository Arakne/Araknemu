package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;

/**
 * Slot for default position
 * This is a Null-Object which never fail
 * For a fail-always object, use {@link NullSlot}
 */
final public class DefaultSlot implements InventorySlot {
    @Override
    public int id() {
        return ItemEntry.DEFAULT_POSITION;
    }

    @Override
    public InventoryEntry entry() {
        return null;
    }

    @Override
    public boolean check(Item item, int quantity) {
        return true;
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {}

    @Override
    public boolean hasEquipment() {
        return false;
    }
}
