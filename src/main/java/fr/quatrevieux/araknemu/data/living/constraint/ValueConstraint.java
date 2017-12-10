package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Constraint on value extracted from entity
 *
 * @param <T> The entity type
 * @param <E> The error type
 * @param <V> The value type
 */
abstract public class ValueConstraint<T, E, V> implements EntityConstraint<T, E> {
    public interface Getter<T, V> {
        /**
         * Get the value from the entity
         */
        public V get(T entity);
    }

    final private E error;
    final private Getter<T, V> getter;

    public ValueConstraint(E error, Getter<T, V> getter) {
        this.error = error;
        this.getter = getter;
    }

    @Override
    public boolean check(T entity) {
        return checkValue(getter.get(entity));
    }

    @Override
    public E error() {
        return error;
    }

    abstract protected boolean checkValue(V value);
}
