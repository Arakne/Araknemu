package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache repository for {@link NpcTemplate}
 */
final public class NpcTemplateRepositoryCache implements NpcTemplateRepository {
    final private NpcTemplateRepository repository;

    final private ConcurrentMap<Integer, NpcTemplate> cacheById = new ConcurrentHashMap<>();

    public NpcTemplateRepositoryCache(NpcTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public NpcTemplate get(int id) {
        if (!cacheById.containsKey(id)) {
            cacheById.put(id, repository.get(id));
        }

        return cacheById.get(id);
    }

    @Override
    public Collection<NpcTemplate> all() {
        Collection<NpcTemplate> loaded = repository.all();

        for (NpcTemplate template : loaded) {
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
    public NpcTemplate get(NpcTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(NpcTemplate entity) throws RepositoryException {
        return
            cacheById.containsKey(entity.id())
            || repository.has(entity)
        ;
    }
}
