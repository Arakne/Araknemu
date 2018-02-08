package fr.quatrevieux.araknemu.game.event.listener.player.inventory;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

/**
 * Save to database the object quantity
 */
final public class SaveItemQuantity implements Listener<ObjectQuantityChanged> {
    final private PlayerItemRepository repository;

    public SaveItemQuantity(PlayerItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(ObjectQuantityChanged event) {
        if (!(event.entry() instanceof InventoryEntry)) {
            return;
        }

        InventoryEntry entry = (InventoryEntry) event.entry();

        repository.update(entry.entity());
    }

    @Override
    public Class<ObjectQuantityChanged> event() {
        return ObjectQuantityChanged.class;
    }
}
