package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.type.Equipment;

import java.util.ArrayList;
import java.util.Collection;

/**
 * All inventory slots
 */
final public class InventorySlots {
    final private Dispatcher dispatcher;

    final private InventorySlot defaultSlot = new DefaultSlot();
    final private InventorySlot[] slots = new InventorySlot[49];

    public InventorySlots(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        add(new AmuletSlot(dispatcher));
        add(new WeaponSlot(dispatcher));
        add(new RingSlot(dispatcher, RingSlot.RING1));
        add(new RingSlot(dispatcher, RingSlot.RING2));
        add(new BeltSlot(dispatcher));
        add(new BootsSlot(dispatcher));
        add(new HelmetSlot(dispatcher));
        add(new MantleSlot(dispatcher));
        add(new NullSlot(8)); // pet

        for (int id : DofusSlot.SLOT_IDS) {
            add(new DofusSlot(dispatcher, id));
        }

        for (int i = 15; i < slots.length; ++i) {
            add(new NullSlot(i));
        }
    }

    /**
     * Get an inventory slot
     */
    public InventorySlot get(int id) throws InventoryException {
        if (id == ItemEntry.DEFAULT_POSITION) {
            return defaultSlot;
        }

        if (id < -1 || id >= slots.length) {
            throw new InventoryException("Invalid slot");
        }

        return slots[id];
    }

    /**
     * Get current equipments
     */
    public Collection<Equipment> equipments() {
        Collection<Equipment> equipments = new ArrayList<>();

        for (InventorySlot slot : slots) {
            if (slot.hasEquipment()) {
                equipments.add(slot.equipment());
            }
        }

        return equipments;
    }

    private void add(InventorySlot slot) {
        slots[slot.id()] = slot;
    }
}
