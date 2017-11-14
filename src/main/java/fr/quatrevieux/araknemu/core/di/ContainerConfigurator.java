package fr.quatrevieux.araknemu.core.di;

import fr.quatrevieux.araknemu.core.di.item.FactoryItem;

/**
 *
 */
public interface ContainerConfigurator {
    /**
     * Register an instance to the container
     *
     * @param object Instance to register
     *
     * @return this
     */
    public ContainerConfigurator set(Object object);

    /**
     * Register an instance to the container, at the given type
     *
     * @param type Type to register
     * @param object Instance to register
     *
     * @return this
     */
    public <T> ContainerConfigurator set(Class<T> type, T object);

    /**
     * Add a factory to the container
     *
     * @param type Type to register
     * @param factory The factory
     *
     * @return this
     */
    public <T> ContainerConfigurator factory(Class<T> type, FactoryItem.Factory<T> factory);

    /**
     * Add a factory to the container and persist (i.e. cache) the value
     *
     * @param type Type to register
     * @param factory The factory
     *
     * @return this
     */
    public <T> ContainerConfigurator persist(Class<T> type, FactoryItem.Factory<T> factory);
}
