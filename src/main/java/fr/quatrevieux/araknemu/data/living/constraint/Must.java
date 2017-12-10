package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Aggregation constraint, the entity MUST satisfy all constraints
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
final public class Must<T, E> implements EntityConstraint<T, E> {
    final private EntityConstraint<T, E>[] constraints;
    private E error;

    public Must(EntityConstraint<T, E>[] constraints) {
        this.constraints = constraints;
    }

    @Override
    public boolean check(T entity) {
        for (EntityConstraint<T, E> constraint : constraints) {
            if (!constraint.check(entity)) {
                error = constraint.error();
                return false;
            }
        }

        error = null;
        return true;
    }

    @Override
    public E error() {
        return error;
    }
}
