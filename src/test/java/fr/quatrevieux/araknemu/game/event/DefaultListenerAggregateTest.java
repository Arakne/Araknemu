package fr.quatrevieux.araknemu.game.event;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DefaultListenerAggregateTest {
    private DefaultListenerAggregate dispatcher;

    class A {}
    class B {}

    class ListenerA implements Listener<A> {
        public A a;

        @Override
        public void on(A event) {
            a = event;
        }

        @Override
        public Class<A> event() {
            return A.class;
        }
    }

    class ListenerA2 extends ListenerA {}

    class ListenerB implements Listener<B> {
        public B b;

        @Override
        public void on(B event) {
            b = event;
        }

        @Override
        public Class<B> event() {
            return B.class;
        }
    }

    @BeforeEach
    void setUp() {
        dispatcher = new DefaultListenerAggregate();
    }

    @Test
    void dispatchNoListenerRegister() {
        dispatcher.dispatch(new A());
    }

    @Test
    void dispatchListenerNotMatching() {
        dispatcher.add(new ListenerA());
        dispatcher.dispatch(new B());
    }

    @Test
    void dispatchWithListener() {
        ListenerA listenerA = new ListenerA();

        dispatcher.add(listenerA);

        A a = new A();
        dispatcher.dispatch(a);

        assertSame(a, listenerA.a);
    }

    @Test
    void dispatchMultipleListener() {
        ListenerA l1 = new ListenerA();
        ListenerA l2 = new ListenerA2();
        ListenerB l3 = new ListenerB();

        dispatcher.add(l1);
        dispatcher.add(l2);
        dispatcher.add(l3);

        A a = new A();

        dispatcher.dispatch(a);

        assertSame(a, l1.a);
        assertSame(a, l2.a);
        assertNull(l3.b);
    }

    @Test
    void has() {
        assertFalse(dispatcher.has(ListenerA.class));

        dispatcher.add(new ListenerA());

        assertTrue(dispatcher.has(ListenerA.class));
    }

    @Test
    void removeNotRegister() {
        dispatcher.remove(ListenerA.class);
    }

    @Test
    void remove() {
        dispatcher.add(new ListenerA());
        dispatcher.remove(ListenerA.class);

        assertFalse(dispatcher.has(ListenerA.class));
        dispatcher.dispatch(new A());
    }

    @Test
    void get() {
        ListenerA listenerA = new ListenerA();

        dispatcher.add(listenerA);

        assertSame(listenerA, dispatcher.get(ListenerA.class));
    }

    @Test
    void addSimpleListener() {
        AtomicReference<A> ref = new AtomicReference<>();

        dispatcher.add(A.class, ref::set);
        A a = new A();

        dispatcher.dispatch(a);

        assertSame(a, ref.get());
    }
}
