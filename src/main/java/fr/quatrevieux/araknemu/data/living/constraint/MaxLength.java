package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Constraint for maximal length
 *
 * @param <T> Entity type
 * @param <E> Error type
 */
final public class MaxLength<T, E> extends ValueConstraint<T, E, String> {
    final private int length;

    public MaxLength(E error, Getter<T, String> getter, int length) {
        super(error, getter);
        this.length = length;
    }

    @Override
    protected boolean checkValue(String value) {
        return value.length() <= length;
    }
}
