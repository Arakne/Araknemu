package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;

/**
 * Delete from database the object
 */
final public class SaveDeletedItem implements Listener<ObjectDeleted> {
    final private PlayerItemRepository repository;

    public SaveDeletedItem(PlayerItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(ObjectDeleted event) {
        if (!(event.entry() instanceof InventoryEntry)) {
            return;
        }

        InventoryEntry entry = (InventoryEntry) event.entry();

        repository.delete(entry.entity());
    }

    @Override
    public Class<ObjectDeleted> event() {
        return ObjectDeleted.class;
    }
}
