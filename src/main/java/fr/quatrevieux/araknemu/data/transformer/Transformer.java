package fr.quatrevieux.araknemu.data.transformer;

/**
 * Interface for data transformer
 *
 * @param <T> The value type to transform
 */
public interface Transformer<T> {
    /**
     * Serialize the value to a simple string
     *
     * If the value is null, the method should return null
     */
    public String serialize(T value);

    /**
     * Parse the serialized string to the original object value
     *
     * If the value is null, the method should return null
     */
    public T unserialize(String serialize);
}
