package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Constraint for an entity
 *
 * EntityConstraint<> constraint = new ...;
 *
 * if (!constraint.value(entity)) {
 *     throw new MyError(constraint.error());
 * }
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
public interface EntityConstraint<T, E> {
    /**
     * Check the entity
     *
     * @param entity entity to value
     *
     * @return true is the entity is valid
     */
    public boolean check(T entity);

    /**
     * Get the error information
     */
    public E error();
}
