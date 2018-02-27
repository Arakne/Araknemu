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
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        InventoryEntry newEntry = slot.set(entry);

        dispatcher.dispatch(new EquipmentChanged(entry, id(), true));

        return newEntry;
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        InventoryEntry entry = slot.set(item, quantity);

        dispatcher.dispatch(new EquipmentChanged(entry, id(), true));

        return entry;
    }

    @Override
    public void unset() {
        InventoryEntry entry = entry();
        slot.unset();

        dispatcher.dispatch(new EquipmentChanged(entry, id(), false));
    }

    @Override
    public void uncheckedSet(InventoryEntry entry) {
        slot.uncheckedSet(entry);
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        slot.check(item, quantity);
    }

    @Override
    public boolean hasEquipment() {
        return slot.entry() != null;
    }
}
