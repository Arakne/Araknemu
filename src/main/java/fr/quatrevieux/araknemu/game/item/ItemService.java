package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.factory.ItemFactory;
import fr.quatrevieux.araknemu.game.world.item.Item;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Service for handle items
 */
final public class ItemService implements PreloadableService {
    final private ItemTemplateRepository repository;
    final private ItemFactory factory;
    final private ItemSetRepository itemSetRepository;

    final private EffectToCharacteristicMapping characteristicMapping;
    final private EffectToSpecialMapping specialMapping;

    final private ConcurrentMap<Integer, GameItemSet> itemSetsById = new ConcurrentHashMap<>();

    public ItemService(ItemTemplateRepository repository, ItemFactory factory, ItemSetRepository itemSetRepository, EffectToCharacteristicMapping characteristicMapping, EffectToSpecialMapping specialMapping) {
        this.repository = repository;
        this.factory = factory;
        this.itemSetRepository = itemSetRepository;

        this.characteristicMapping = characteristicMapping;
        this.specialMapping = specialMapping;
    }

    @Override
    public void preload(Logger logger) {
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
                    effects.stream()
                        .filter(entry -> entry.effect().type() == Effect.Type.CHARACTERISTIC)
                        .map(e -> characteristicMapping.create(e.effect(), e.min()))
                        .collect(Collectors.toList())
                    ,
                    effects.stream()
                        .filter(entry -> entry.effect().type() == Effect.Type.SPECIAL)
                        .map(e -> specialMapping.create(e, false))
                        .collect(Collectors.toList())
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
}
