package fr.quatrevieux.araknemu.core.di.item;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;

/**
 * Container item using a factory to create de instance
 *
 * @param <T> Item type
 */
final public class FactoryItem<T> implements ContainerItem<T> {
    public interface Factory<T> {
        /**
         * Instantiate the value
         *
         * @param container The DI container instance
         *
         * @return The new instance
         */
        public T make(Container container) throws ContainerException;
    }

    final private Class<T> type;
    final private Factory<T> factory;

    public FactoryItem(Class<T> type, Factory<T> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public T value(Container container) throws ContainerException {
        return factory.make(container);
    }
}
