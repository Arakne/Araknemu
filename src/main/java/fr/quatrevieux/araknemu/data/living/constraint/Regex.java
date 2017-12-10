package fr.quatrevieux.araknemu.data.living.constraint;

import java.util.regex.Pattern;

/**
 * Perform a regex value on the value
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
final public class Regex<T, E> extends ValueConstraint<T, E, String> {
    final private Pattern regex;

    public Regex(E error, Getter<T, String> getter, Pattern regex) {
        super(error, getter);

        this.regex = regex;
    }

    public Regex(E error, Getter<T, String> getter, String regex) {
        this(error, getter, Pattern.compile(regex));
    }

    @Override
    protected boolean checkValue(String value) {
        return regex.matcher(value).matches();
    }
}
