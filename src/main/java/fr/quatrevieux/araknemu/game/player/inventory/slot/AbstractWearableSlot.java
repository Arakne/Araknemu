package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.*;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.type.Wearable;

/**
 * Base slot class for wearable
 */
abstract public class AbstractWearableSlot extends AbstractEquipmentSlot {
    public AbstractWearableSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner, int id, Type type) {
        super(
            dispatcher,
            new SimpleSlot(
                id,
                new SlotConstraint[] {
                    new SingleItemConstraint(),
                    new ItemClassConstraint(Wearable.class),
                    new ItemTypeConstraint(type),
                    new EquipmentLevelConstraint(owner)
                },
                storage
            )
        );
    }
}
