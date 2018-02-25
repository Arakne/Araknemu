package fr.quatrevieux.araknemu.game.event;

import java.util.*;

/**
 * Default implementation for dispatcher, using maps
 *
 * @fixme Use ConcurrentHashMap instead of HashMap ?
 */
final public class DefaultListenerAggregate implements ListenerAggregate {
    final private Map<Class<? extends Listener>, Listener> listeners = new HashMap<>();
    final private Map<Class, Set<Class<? extends Listener>>> events = new HashMap<>();

    @Override
    public void dispatch(Object event) {
        if (!events.containsKey(event.getClass())) {
            return;
        }

        for (Class listenerClass : events.get(event.getClass())) {
            get(listenerClass).on(event);
        }
    }

    @Override
    public void add(Listener listener) {
        if (!events.containsKey(listener.event())) {
            events.put(listener.event(), new LinkedHashSet<>());
        }

        listeners.put(listener.getClass(), listener);
        events.get(listener.event()).add(listener.getClass());
    }

    @Override
    public boolean has(Class<? extends Listener> listenerClass) {
        return listeners.containsKey(listenerClass);
    }

    @Override
    public void remove(Class<? extends Listener> listenerClass) {
        if (!has(listenerClass)) {
            return;
        }

        Listener listener = listeners.remove(listenerClass);
        events.get(listener.event()).remove(listenerClass);
    }

    @Override
    public <E extends Listener> E get(Class<E> listenerClass) {
        return (E) listeners.get(listenerClass);
    }
}
