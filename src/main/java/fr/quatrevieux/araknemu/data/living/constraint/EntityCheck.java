package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Check for entire entity value
 *
 * @param <T> Entity type
 * @param <E> Error type
 */
final public class EntityCheck<T, E> implements EntityConstraint<T, E> {
    public interface Checker<T> {
        public boolean check(T entity);
    }

    final private E error;
    final private Checker<T> checker;

    public EntityCheck(E error, Checker<T> checker) {
        this.error = error;
        this.checker = checker;
    }

    @Override
    public boolean check(T entity) {
        return checker.check(entity);
    }

    @Override
    public E error() {
        return error;
    }
}
