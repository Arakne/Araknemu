package fr.quatrevieux.araknemu.game.world.item.inventory;

import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectAdded;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectDeleted;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;

import java.util.*;

/**
 * Simple implementation for {@link ItemStorage}
 */
final public class SimpleItemStorage<E extends ItemEntry> implements ItemStorage<E> {
    public interface EntryFactory<E> {
        /**
         * Create the inventory entry for the current item storage
         */
        public E create(int id, Item item, int quantity, int position);
    }

    final private Dispatcher dispatcher;
    final private EntryFactory<E> factory;

    final private Map<Integer, E> items = new HashMap<>();

    private int lastId = 0;

    public SimpleItemStorage(Dispatcher dispatcher, EntryFactory<E> factory, Collection<E> items) {
        this.dispatcher = dispatcher;
        this.factory = factory;

        for (E entry : items) {
            push(entry);

            if (entry.id() > lastId) {
                lastId = entry.id();
            }
        }
    }

    public SimpleItemStorage(Dispatcher dispatcher, EntryFactory<E> factory) {
        this(dispatcher, factory, Collections.emptyList());
    }

    @Override
    public E get(int id) throws ItemNotFoundException {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException(id);
        }

        return items.get(id);
    }

    @Override
    public E add(Item item, int quantity, int position) {
        E entry = factory.create(++lastId, item, quantity, position);

        push(entry);
        dispatcher.dispatch(new ObjectAdded(entry));

        return entry;
    }

    @Override
    public E delete(int id) throws ItemNotFoundException {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException(id);
        }

        E entry = items.remove(id);

        dispatcher.dispatch(new ObjectDeleted(entry));

        return entry;
    }

    @Override
    public Iterator<E> iterator() {
        return items.values().iterator();
    }

    private void push(E entry) {
        items.put(entry.id(), entry);
    }
}
