package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.world.item.Item;
import org.slf4j.Logger;

import java.util.List;

/**
 * Service for handle items
 */
final public class ItemService implements PreloadableService {
    final private ItemTemplateRepository repository;
    final private ItemFactory factory;

    public ItemService(ItemTemplateRepository repository, ItemFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading items...");

        int loaded = repository.load().size();

        logger.info("Successfully load {} items", loaded);
    }

    /**
     * Create a new item
     *
     * @param id The item template id
     * @param maximize Maximize item stats ?
     */
    public Item create(int id, boolean maximize) {
        return factory.create(repository.get(id), maximize);
    }

    /**
     * Create a new item
     *
     * @param id The item template id
     */
    public Item create(int id) {
        return create(id, false);
    }

    /**
     * Retrieve an item with its effects
     *
     * @param id The item template id
     * @param effects The item effects
     */
    public Item retrieve(int id, List<ItemTemplateEffectEntry> effects) {
        return factory.retrieve(repository.get(id), effects);
    }
}
