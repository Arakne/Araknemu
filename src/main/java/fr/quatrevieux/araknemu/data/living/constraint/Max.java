package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Check for maximum value
 *
 * @param <T> The entity type
 * @param <E> The error type
 * @param <V> The value type (should be comparable
 */
final public class Max<T, E, V extends Comparable> extends ValueConstraint<T, E, V> {
    final private V value;

    public Max(E error, Getter<T, V> getter, V value) {
        super(error, getter);
        this.value = value;
    }

    @Override
    protected boolean checkValue(V value) {
        return value.compareTo(this.value) <= 0;
    }
}
