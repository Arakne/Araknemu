package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.world.item.Item;
import org.slf4j.Logger;

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
        ItemTemplate template = repository.get(id);

        return factory.create(template, maximize);
    }

    /**
     * Create a new item
     *
     * @param id The item template id
     */
    public Item create(int id) {
        return create(id, false);
    }
}
