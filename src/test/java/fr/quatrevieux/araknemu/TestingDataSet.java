package fr.quatrevieux.araknemu;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class TestingDataSet {
    final private Container repositories;
    final private Map<Class, Class<? extends Repository>> classMap = new HashMap<>();
    final private Set<Repository> usedRepositories = new HashSet<>();
    final private Map<String, Object> entities = new HashMap<>();

    public TestingDataSet(Container repositories) {
        this.repositories = repositories;
    }

    public <T> TestingDataSet declare(Class<T> entityType, Class<? extends Repository<T>> repositoryClass) {
        classMap.put(entityType, repositoryClass);

        return this;
    }

    public TestingDataSet use(Class... entityClasses) throws ContainerException {
        for (Class clazz : entityClasses) {
            repository(clazz);
        }

        return this;
    }

    public <T> Repository<T> repository(Class<T> entityClass) throws ContainerException {
        Repository<T> repository = repositories.get(classMap.get(entityClass));

        if (!usedRepositories.contains(repository)) {
            repository.initialize();
            usedRepositories.add(repository);
        }

        return repository;
    }

    public <T> Repository<T> repository(T entity) throws ContainerException {
        return (Repository<T>) repository(entity.getClass());
    }

    public <T> T push(T entity, String identifier) throws ContainerException {
        MutableRepository<T> repository = MutableRepository.class.cast(repository(entity));

        entity = repository.add(entity);

        entities.put(identifier, entity);

        return entity;
    }

    public <T> T push(T entity) throws ContainerException {
        return push(entity, entity.toString());
    }

    public Object get(String identifier) {
        return entities.get(identifier);
    }

    public <T> T refresh(T entity) throws ContainerException {
        return repository(entity).get(entity);
    }

    public TestingDataSet destroy() throws ContainerException {
        for (Repository repository : usedRepositories) {
            repository.destroy();
        }

        usedRepositories.clear();
        entities.clear();

        return this;
    }
}
