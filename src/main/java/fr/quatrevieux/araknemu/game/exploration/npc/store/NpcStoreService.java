package fr.quatrevieux.araknemu.game.exploration.npc.store;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.item.ItemService;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Handle Npc stores
 */
final public class NpcStoreService {
    final private ItemService itemService;
    final private GameConfiguration.EconomyConfiguration configuration;
    final private ItemTemplateRepository itemTemplateRepository;

    /**
     * Already loaded stores
     * Indexed by the npc template id
     */
    final private Map<Integer, NpcStore> stores = new ConcurrentHashMap<>();

    public NpcStoreService(ItemService itemService, ItemTemplateRepository itemTemplateRepository, GameConfiguration.EconomyConfiguration configuration) {
        this.itemService = itemService;
        this.itemTemplateRepository = itemTemplateRepository;
        this.configuration = configuration;
    }

    /**
     * Load the store for the given npc template
     *
     * @return The store, if available
     */
    public Optional<NpcStore> load(NpcTemplate template) {
        if (stores.containsKey(template.id())) {
            return Optional.of(stores.get(template.id()));
        }

        return template.storeItems()
            .map(items -> {
                NpcStore store = new NpcStore(
                    itemService,
                    configuration,
                    Arrays.stream(items)
                        .mapToObj(itemTemplateRepository::get)
                        .collect(Collectors.toList())
                );

                stores.put(template.id(), store);

                return store;
            })
        ;
    }
}
