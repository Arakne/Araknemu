package fr.quatrevieux.araknemu.core.di.item;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;

/**
 * Item of a container
 * @param <T> Contained item
 */
public interface ContainerItem<T> {
    /**
     * The item type
     */
    public Class<T> type();

    /**
     * Get or instantiate the value
     *
     * @param container The DI container
     *
     * @return The contained value
     */
    public T value(Container container) throws ContainerException;
}
