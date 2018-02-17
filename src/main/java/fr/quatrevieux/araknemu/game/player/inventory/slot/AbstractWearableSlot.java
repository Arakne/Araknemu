package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemClassConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemTypeConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SingleItemConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.type.Wearable;

/**
 * Base slot class for wearable
 */
abstract public class AbstractWearableSlot extends AbstractEquipmentSlot {
    public AbstractWearableSlot(Dispatcher dispatcher, int id, Type type) {
        super(
            dispatcher,
            new SimpleSlot(id, new SlotConstraint[] {
                new SingleItemConstraint(),
                new ItemClassConstraint(Wearable.class),
                new ItemTypeConstraint(type)
            })
        );
    }
}
