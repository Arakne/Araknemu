package fr.quatrevieux.araknemu.core.di;

/**
 * Dependency injection container
 */
public interface Container {
    /**
     * Get instance from the container
     * The return value of get, with same type SHOULD return equals value
     * The value MAY reference to another instance
     *
     * @param type Type to find
     * @param <T> Type
     *
     * @return The contained instance
     *
     * @throws ItemNotFoundException When cannot found given type
     * @throws ContainerException When cannot instantiate the element
     */
    public <T> T get(Class<T> type) throws ContainerException;

    /**
     * Check if the container contains the type
     *
     * @param type Type to find
     *
     * @return true if the contains contains the type
     */
    public boolean has(Class type);

    /**
     * Register a module into the container
     *
     * @param module Module to register
     */
    public void register(ContainerModule module);
}
