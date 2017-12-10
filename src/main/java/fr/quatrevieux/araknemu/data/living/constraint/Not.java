package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Reverse constraint check result
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
final public class Not<T, E> implements EntityConstraint<T, E> {
    final private EntityConstraint<T, E> constraint;

    public Not(EntityConstraint<T, E> constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean check(T entity) {
        return !constraint.check(entity);
    }

    @Override
    public E error() {
        return constraint.error();
    }
}
