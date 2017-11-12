package fr.quatrevieux.araknemu.core.dbal.repository;

/**
 * Base repository interface
 * @param <E> The handled entity type
 */
public interface Repository<E> {
    /**
     * Initialize repository
     */
    public void initialize() throws RepositoryException;

    /**
     * Get an entity from the repository
     *
     * @param entity The entity to find (used as primary key criteria)
     *
     * @return The repository entity. The returned value MUST NOT be null
     *
     * @throws RepositoryException When an error occurs during retrieving data
     * @throws EntityNotFoundException When the entity cannot be found
     *
     * @see Repository#has(Object) To check if the entity exists
     */
    public E get(E entity) throws RepositoryException;

    /**
     * Check if an entity exists in the repository
     *
     * @param entity The entity to find (used as primary key criteria)
     *
     * @return true if exists
     *
     * @throws RepositoryException When an error occurs during retrieving data
     */
    public boolean has(E entity) throws RepositoryException;
}
