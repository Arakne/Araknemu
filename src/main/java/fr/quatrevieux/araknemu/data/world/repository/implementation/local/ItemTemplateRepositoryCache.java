package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate}
 */
final public class ItemTemplateRepositoryCache implements ItemTemplateRepository {
    final private ItemTemplateRepository repository;

    final private ConcurrentMap<Integer, ItemTemplate> cacheById = new ConcurrentHashMap<>();

    public ItemTemplateRepositoryCache(ItemTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public ItemTemplate get(int id) {
        if (!cacheById.containsKey(id)) {
            cacheById.put(id, repository.get(id));
        }

        return cacheById.get(id);
    }

    @Override
    public Collection<ItemTemplate> load() {
        Collection<ItemTemplate> loaded = repository.load();

        for (ItemTemplate template : loaded) {
            cacheById.put(template.id(), template);
        }

        return loaded;
    }

    @Override
    public void initialize() throws RepositoryException {
        repository.initialize();
    }

    @Override
    public void destroy() throws RepositoryException {
        repository.destroy();
    }

    @Override
    public ItemTemplate get(ItemTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(ItemTemplate entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}
