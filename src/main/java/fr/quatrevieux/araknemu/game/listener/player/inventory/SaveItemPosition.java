package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectMoved;

/**
 * Save to database the object position
 */
final public class SaveItemPosition implements Listener<ObjectMoved> {
    final private PlayerItemRepository repository;

    public SaveItemPosition(PlayerItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(ObjectMoved event) {
        if (!(event.entry() instanceof InventoryEntry)) {
            return;
        }

        InventoryEntry entry = (InventoryEntry) event.entry();

        repository.update(entry.entity());
    }

    @Override
    public Class<ObjectMoved> event() {
        return ObjectMoved.class;
    }
}
