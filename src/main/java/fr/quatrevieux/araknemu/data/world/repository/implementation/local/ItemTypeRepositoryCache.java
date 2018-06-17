package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link ItemType}
 */
final public class ItemTypeRepositoryCache implements ItemTypeRepository {
    final private ItemTypeRepository repository;

    final private ConcurrentMap<Integer, ItemType> cacheById = new ConcurrentHashMap<>();

    public ItemTypeRepositoryCache(ItemTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public ItemType get(int id) {
        if (!cacheById.containsKey(id)) {
            cacheById.put(id, repository.get(id));
        }

        return cacheById.get(id);
    }

    @Override
    public Collection<ItemType> load() {
        Collection<ItemType> loaded = repository.load();

        for (ItemType template : loaded) {
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
    public ItemType get(ItemType entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(ItemType entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}
