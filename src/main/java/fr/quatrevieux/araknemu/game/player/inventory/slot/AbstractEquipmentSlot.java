package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;

/**
 * Base slot class for equipments
 */
abstract public class AbstractEquipmentSlot implements InventorySlot {
    final private Dispatcher dispatcher;
    final private InventorySlot slot;

    public AbstractEquipmentSlot(Dispatcher dispatcher, InventorySlot slot) {
        this.dispatcher = dispatcher;
        this.slot = slot;
    }

    @Override
    public int id() {
        return slot.id();
    }

    @Override
    public InventoryEntry entry() {
        return slot.entry();
    }

    @Override
    public void set(InventoryEntry entry) throws InventoryException {
        slot.set(entry);

        dispatcher.dispatch(new EquipmentChanged(entry, id()));
    }

    @Override
    public void unset() {
        InventoryEntry entry = entry();
        slot.unset();

        dispatcher.dispatch(new EquipmentChanged(entry, id()));
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {
        slot.uncheckedSet(entry);
    }

    @Override
    public boolean check(Item item, int quantity) {
        return slot.check(item, quantity);
    }

    @Override
    public boolean hasEquipment() {
        return slot.entry() != null;
    }
}
