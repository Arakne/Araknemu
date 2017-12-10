package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Check for value not empty (or null) string
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
final public class NotEmpty<T, E> extends ValueConstraint<T, E, String> {
    public NotEmpty(E error, Getter<T, String> getter) {
        super(error, getter);
    }

    @Override
    protected boolean checkValue(String value) {
        return value != null && !value.isEmpty();
    }
}
