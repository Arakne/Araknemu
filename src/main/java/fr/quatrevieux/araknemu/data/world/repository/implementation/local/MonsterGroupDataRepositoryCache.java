package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link MonsterGroupData}
 */
final public class MonsterGroupDataRepositoryCache implements MonsterGroupDataRepository {
    final private MonsterGroupDataRepository repository;

    final private ConcurrentMap<Integer, MonsterGroupData> cacheById = new ConcurrentHashMap<>();

    public MonsterGroupDataRepositoryCache(MonsterGroupDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public MonsterGroupData get(int id) {
        if (!cacheById.containsKey(id)) {
            cacheById.put(id, repository.get(id));
        }

        return cacheById.get(id);
    }

    @Override
    public List<MonsterGroupData> all() {
        List<MonsterGroupData> loaded = repository.all();

        for (MonsterGroupData template : loaded) {
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
    public MonsterGroupData get(MonsterGroupData entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(MonsterGroupData entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}
