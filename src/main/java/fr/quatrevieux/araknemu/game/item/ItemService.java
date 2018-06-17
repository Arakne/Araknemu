package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle items
 */
final public class ItemService implements PreloadableService {
    final private ItemTemplateRepository repository;
    final private ItemFactory factory;
    final private ItemSetRepository itemSetRepository;
    final private ItemTypeRepository itemTypeRepository;
    final private EffectMappers mappers;

    final private ConcurrentMap<Integer, GameItemSet> itemSetsById = new ConcurrentHashMap<>();

    public ItemService(ItemTemplateRepository repository, ItemFactory factory, ItemSetRepository itemSetRepository, ItemTypeRepository itemTypeRepository, EffectMappers mappers) {
        this.repository = repository;
        this.factory = factory;
        this.itemSetRepository = itemSetRepository;
        this.itemTypeRepository = itemTypeRepository;
        this.mappers = mappers;
    }

    @Override
    public void preload(Logger logger) {
        loadItemTypes(logger);
        loadItemSets(logger);
        loadItems(logger);
    }

    /**
     * Create a new item
     *
     * @param id The item template id
     * @param maximize Maximize item stats ?
     */
    public Item create(int id, boolean maximize) {
        ItemTemplate template = repository.get(id);

        return factory.create(
            template,
            itemTypeRepository.get(template.type()),
            template.itemSet() == 0
                ? null
                : itemSet(template.itemSet())
            ,
            maximize
        );
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
        ItemTemplate template = repository.get(id);

        return factory.retrieve(
            template,
            itemTypeRepository.get(template.type()),
            template.itemSet() == 0
                ? null
                : itemSet(template.itemSet())
            ,
            effects
        );
    }

    /**
     * Get an item set
     *
     * @param id The item set id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When the item set do not exists
     */
    public GameItemSet itemSet(int id) {
        if (!itemSetsById.containsKey(id)) {
            itemSetsById.put(id, createItemSet(itemSetRepository.get(id)));
        }

        return itemSetsById.get(id);
    }

    private GameItemSet createItemSet(ItemSet entity) {
        List<GameItemSet.Bonus> bonuses = new ArrayList<>();

        for (List<ItemTemplateEffectEntry> effects : entity.bonus()) {
            bonuses.add(
                new GameItemSet.Bonus(
                    effects,
                    mappers.get(CharacteristicEffect.class).create(effects),
                    mappers.get(SpecialEffect.class).create(effects)
                )
            );
        }

        return new GameItemSet(entity, bonuses);
    }

    public void loadItems(Logger logger) {
        logger.info("Loading items...");

        int loaded = repository.load().size();

        logger.info("Successfully load {} items", loaded);
    }

    public void loadItemSets(Logger logger) {
        logger.info("Loading item sets...");

        for (ItemSet entity : itemSetRepository.load()) {
            itemSetsById.put(entity.id(), createItemSet(entity));
        }

        logger.info("Successfully load {} item sets", itemSetsById.size());
    }

    public void loadItemTypes(Logger logger) {
        logger.info("Loading item types...");
        logger.info("Successfully load {} item sets", itemTypeRepository.load().size());
    }
}
