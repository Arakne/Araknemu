package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Check value using lambda
 *
 * @param <T> The entity type
 * @param <E> The error type
 * @param <V> The value type
 */
final public class ValueCheck<T, E, V> extends ValueConstraint<T, E, V> {
    public interface Checker<V> {
        public boolean check(V value);
    }

    final private Checker<V> checker;

    public ValueCheck(E error, Getter<T, V> getter, Checker<V> checker) {
        super(error, getter);
        this.checker = checker;
    }

    @Override
    protected boolean checkValue(V value) {
        return checker.check(value);
    }
}
