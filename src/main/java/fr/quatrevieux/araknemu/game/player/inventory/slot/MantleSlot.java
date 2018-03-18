package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.*;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.type.Wearable;

/**
 * Slot for mantle
 */
final public class MantleSlot extends AbstractEquipmentSlot {
    final static public int SLOT_ID = 7;

    public MantleSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner) {
        super(
            dispatcher,
            new SimpleSlot(
                SLOT_ID,
                new SlotConstraint[] {
                    new SingleItemConstraint(),
                    new ItemClassConstraint(Wearable.class),
                    new ItemTypeSetConstraint(Type.CAPE, Type.SAC_DOS),
                    new EquipmentLevelConstraint(owner)
                },
                storage
            )
        );
    }
}