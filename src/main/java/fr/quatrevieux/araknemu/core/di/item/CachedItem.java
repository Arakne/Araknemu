package fr.quatrevieux.araknemu.core.di.item;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;

/**
 * Decorate a container item to cache its value (useful for persistent values)
 * @param <T> Item type
 */
final public class CachedItem<T> implements ContainerItem<T> {
    final private ContainerItem<T> inner;
    private T value;

    public CachedItem(ContainerItem<T> inner) {
        this.inner = inner;
    }

    @Override
    public Class<T> type() {
        return inner.type();
    }

    @Override
    public T value(Container container) throws ContainerException {
        if (value == null) {
            value = inner.value(container);
        }

        return value;
    }
}
