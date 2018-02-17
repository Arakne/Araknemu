package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemClassConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SingleItemConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.type.Weapon;

/**
 * Slot for weapons
 */
final public class WeaponSlot extends AbstractEquipmentSlot {
    final static public int SLOT_ID = 1;

    public WeaponSlot(Dispatcher dispatcher) {
        super(
            dispatcher,
            new SimpleSlot(SLOT_ID, new SlotConstraint[] {
                new SingleItemConstraint(),
                new ItemClassConstraint(Weapon.class)
            })
        );
    }
}
