package fr.quatrevieux.araknemu.core.di;

import fr.quatrevieux.araknemu.core.di.item.CachedItem;
import fr.quatrevieux.araknemu.core.di.item.ContainerItem;
import fr.quatrevieux.araknemu.core.di.item.FactoryItem;
import fr.quatrevieux.araknemu.core.di.item.ValueItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Container implementation using {@link fr.quatrevieux.araknemu.core.di.item.ContainerItem}
 */
final public class ItemPoolContainer implements Container {
    /**
     * Configuration class for {@link ItemPoolContainer}
     */
    final public class Configurator implements ContainerConfigurator {
        @Override
        public Configurator set(Object object) {
            ItemPoolContainer.this.set(new ValueItem<>(object));

            return this;
        }

        @Override
        public <T> Configurator set(Class<T> type, T object) {
            ItemPoolContainer.this.set(new ValueItem<>(type, object));

            return this;
        }

        @Override
        public <T> Configurator factory(Class<T> type, FactoryItem.Factory<T> factory) {
            ItemPoolContainer.this.set(new FactoryItem<>(type, factory));

            return this;
        }

        @Override
        public <T> Configurator persist(Class<T> type, FactoryItem.Factory<T> factory) {
            ItemPoolContainer.this.set(new CachedItem<>(
                new FactoryItem<>(type, factory)
            ));

            return this;
        }

        /**
         * Register an item to the container
         *
         * @param item Item to register
         *
         * @return this
         */
        public Configurator item(ContainerItem item) {
            ItemPoolContainer.this.set(item);

            return this;
        }
    }

    final private Map<Class, ContainerItem> items = new HashMap<>();

    @Override
    public <T> T get(Class<T> type) throws ContainerException {
        if (!items.containsKey(type)) {
            throw new ItemNotFoundException(type);
        }

        return (T) items.get(type).value(this);
    }

    @Override
    public boolean has(Class type) {
        return items.containsKey(type);
    }

    @Override
    public void register(ContainerModule module) {
        module.configure(new Configurator());
    }

    /**
     * Get pool configurator
     */
    public Configurator configurator() {
        return new Configurator();
    }

    /**
     * Set a new item to the container
     */
    private void set(ContainerItem item) {
        items.put(item.type(), item);
    }
}
