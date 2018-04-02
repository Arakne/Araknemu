package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.listener.service.AddInventoryListeners;
import fr.quatrevieux.araknemu.game.listener.service.AddInventoryListenersForExploration;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.world.item.Item;

import java.util.stream.Collectors;

/**
 * Service for handle player inventory
 */
final public class InventoryService implements EventsSubscriber {
    static class LoadedItem {
        final private PlayerItem entity;
        final private Item item;

        public LoadedItem(PlayerItem entity, Item item) {
            this.entity = entity;
            this.item = item;
        }

        public PlayerItem entity() {
            return entity;
        }

        public Item item() {
            return item;
        }
    }

    final private PlayerItemRepository repository;
    final private ItemService service;

    public InventoryService(PlayerItemRepository repository, ItemService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new AddInventoryListeners(repository),
            new AddInventoryListenersForExploration()
        };
    }

    /**
     * Load the inventory for the player
     *
     * @param player Player to load
     */
    public PlayerInventory load(Player player) {
        return new PlayerInventory(
            repository.byPlayer(player)
                .stream()
                .map(
                    entity -> new LoadedItem(
                        entity,
                        service.retrieve(entity.itemTemplateId(), entity.effects())
                    )
                )
                .collect(Collectors.toList())
        );
    }
}
