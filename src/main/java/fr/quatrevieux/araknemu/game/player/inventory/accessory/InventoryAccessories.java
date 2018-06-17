package fr.quatrevieux.araknemu.game.player.inventory.accessory;

import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import fr.quatrevieux.araknemu.game.world.creature.accessory.*;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

/**
 * Accessories implementation using inventory
 */
final public class InventoryAccessories extends AbstractAccessories {
    final private InventorySlots slots;

    public InventoryAccessories(InventorySlots slots) {
        this.slots = slots;
    }

    @Override
    public Accessory get(AccessoryType type) {
        InventorySlot slot;

        try {
            slot = slots.get(type.slot());
        } catch (InventoryException e) {
            throw new RuntimeException(e);
        }

        if (!slot.hasEquipment()) {
            return new NullAccessory(type);
        }

        return new InventoryAccessory(slot.entry());
    }
}
