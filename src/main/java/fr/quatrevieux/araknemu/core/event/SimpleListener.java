package fr.quatrevieux.araknemu.core.event;

import java.util.function.Consumer;

/**
 * Simple implementation of listener using {@link Consumer}
 *
 * dispatcher.add(
 *     new SimpleListener<>(MyEvent.class, (evt) -> doSomething(evt))
 * );
 */
final public class SimpleListener<E> implements Listener<E> {
    final private Class<E> type;
    final private Consumer<E> consumer;

    public SimpleListener(Class<E> type, Consumer<E> consumer) {
        this.type = type;
        this.consumer = consumer;
    }

    @Override
    public void on(E event) {
        consumer.accept(event);
    }

    @Override
    public Class<E> event() {
        return type;
    }
}
