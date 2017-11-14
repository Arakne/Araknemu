package fr.quatrevieux.araknemu.core.di;

import fr.quatrevieux.araknemu.core.di.item.ContainerItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Container implementation using {@link fr.quatrevieux.araknemu.core.di.item.ContainerItem}
 */
final public class ItemPoolContainer implements Container {
    final private Map<Class, ContainerItem> items = new HashMap<>();

    @Override
    public <T> T get(Class<T> type) throws ClassNotFoundException {
        if (!items.containsKey(type)) {
            throw new ClassNotFoundException(type);
        }

        return (T) items.get(type).value(this);
    }

    @Override
    public boolean has(Class type) {
        return items.containsKey(type);
    }

    /**
     * Set a new item to the container
     */
    public void set(ContainerItem item) {
        items.put(item.type(), item);
    }
}
