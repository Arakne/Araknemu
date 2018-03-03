package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link ItemSet}
 */
final public class ItemSetRepositoryCache implements ItemSetRepository {
    final private ItemSetRepository repository;

    final private ConcurrentMap<Integer, ItemSet> cacheById = new ConcurrentHashMap<>();

    public ItemSetRepositoryCache(ItemSetRepository repository) {
        this.repository = repository;
    }

    @Override
    public ItemSet get(int id) {
        if (!cacheById.containsKey(id)) {
            cacheById.put(id, repository.get(id));
        }

        return cacheById.get(id);
    }

    @Override
    public Collection<ItemSet> load() {
        Collection<ItemSet> loaded = repository.load();

        for (ItemSet template : loaded) {
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
    public ItemSet get(ItemSet entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(ItemSet entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}
