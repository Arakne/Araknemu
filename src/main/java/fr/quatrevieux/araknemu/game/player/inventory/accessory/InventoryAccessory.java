package fr.quatrevieux.araknemu.game.player.inventory.accessory;

import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Adapt inventory entry to accessory
 */
final public class InventoryAccessory implements Accessory {
    final private ItemEntry entry;

    public InventoryAccessory(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public AccessoryType type() {
        return AccessoryType.bySlot(entry.position());
    }

    @Override
    public int appearance() {
        return entry.templateId();
    }

    @Override
    public String toString() {
        return Integer.toHexString(appearance());
    }
}
