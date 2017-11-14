package fr.quatrevieux.araknemu.core.di.item;

import fr.quatrevieux.araknemu.core.di.Container;

/**
 * Simple value container item
 *
 * @param <T> The value type
 */
final public class ValueItem<T> implements ContainerItem<T> {
    final private Class<T> type;
    final private T value;

    public ValueItem(Class<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    public ValueItem(T value) {
        this((Class<T>) value.getClass(), value);
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public T value(Container container) {
        return value;
    }
}
