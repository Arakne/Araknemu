package fr.quatrevieux.araknemu.core.dbal.repository;

/**
 * Repository for savable entity
 * @param <E> The entity type
 */
public interface MutableRepository<E> extends Repository<E> {
    /**
     * Save the entity into the repository
     * On creation with PK generation (auto-increment), the returned entity will contains the generated PK
     *
     * @param entity Entity to save
     *
     * @return The entity instance related to repository.
     *
     * @throws RepositoryException When error occurs during saving
     */
    public E add(E entity) throws RepositoryException;

    /**
     * Delete an entity from the repository
     *
     * @param entity Entity to delete
     *
     * @throws RepositoryException When error occurs during saving
     */
    public void delete(E entity) throws RepositoryException;
}
