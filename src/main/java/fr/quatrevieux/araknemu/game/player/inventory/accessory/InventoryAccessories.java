package fr.quatrevieux.araknemu.game.player.inventory.accessory;

import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AbstractAccessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.NullAccessory;

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
        return slots.get(type.slot()).entry()
            .<Accessory>map(InventoryAccessory::new)
            .orElseGet(() -> new NullAccessory(type))
        ;
    }
}
