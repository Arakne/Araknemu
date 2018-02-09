package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

/**
 * All inventory slots
 */
final public class InventorySlots {
    final private InventorySlot defaultSlot = new DefaultSlot();
    final private InventorySlot[] slots = new InventorySlot[48];

    public InventorySlots() {
        add(new AmuletSlot());
        add(new WeaponSlot());
        add(new RingSlot(RingSlot.RING1));
        add(new RingSlot(RingSlot.RING2));
        add(new BeltSlot());
        add(new BootsSlot());
        add(new HelmetSlot());
        add(new MantleSlot());
        add(new NullSlot(8)); // pet

        for (int id : DofusSlot.SLOT_IDS) {
            add(new DofusSlot(id));
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

    private void add(InventorySlot slot) {
        slots[slot.id()] = slot;
    }
}
