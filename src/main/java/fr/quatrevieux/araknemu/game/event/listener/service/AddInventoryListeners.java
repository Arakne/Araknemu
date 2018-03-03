package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.inventory.*;
import fr.quatrevieux.araknemu.game.event.listener.player.inventory.itemset.InitializeItemSets;
import fr.quatrevieux.araknemu.game.event.listener.player.inventory.itemset.SendItemSetChange;

/**
 * Add listener for inventory events
 */
final public class AddInventoryListeners implements Listener<PlayerLoaded> {
    final private PlayerItemRepository repository;

    public AddInventoryListeners(PlayerItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(PlayerLoaded event) {
        ListenerAggregate dispatcher = event.player().dispatcher();

        dispatcher.add(new SendItemData(event.player()));
        dispatcher.add(new SendItemPosition(event.player()));
        dispatcher.add(new SendItemQuantity(event.player()));
        dispatcher.add(new SendItemDeleted(event.player()));

        dispatcher.add(new SaveNewItem(repository));
        dispatcher.add(new SaveItemPosition(repository));
        dispatcher.add(new SaveItemQuantity(repository));
        dispatcher.add(new SaveDeletedItem(repository));

        dispatcher.add(new UpdateStuffStats(event.player()));

        dispatcher.add(new InitializeItemSets(event.player()));
        dispatcher.add(new SendItemSetChange(event.player()));
    }

    @Override
    public Class<PlayerLoaded> event() {
        return PlayerLoaded.class;
    }
}
