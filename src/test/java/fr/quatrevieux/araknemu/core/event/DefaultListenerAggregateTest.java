/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.event;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void dispatchWithExceptionShouldNotStopOtherListeners() {
        Logger logger = Mockito.mock(Logger.class);
        dispatcher = new DefaultListenerAggregate(logger);

        RuntimeException error = new RuntimeException("my error");
        Listener<A> l1 = new Listener<A>() {
            @Override
            public void on(A event) {
                throw error;
            }

            @Override
            public Class<A> event() {
                return A.class;
            }
        };
        ListenerA l2 = new ListenerA();

        dispatcher.add(l1);
        dispatcher.add(l2);

        A a = new A();

        dispatcher.dispatch(a);

        assertSame(a, l2.a);
        Mockito.verify(logger).error("Error during execution of listener fr.quatrevieux.araknemu.core.event.DefaultListenerAggregateTest$1", error);
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

    @Test
    void removeListenerDuringDispatchShouldBeTakenInAccountAfterDispatch() {
        final Collection<Class> listeners = new ArrayList<>();

        class L1 implements Listener<A> {
            @Override
            public void on(A event) {
                listeners.add(getClass());
            }

            @Override
            public Class<A> event() {
                return A.class;
            }
        }

        class L2 implements Listener<A> {
            @Override
            public void on(A event) {
                listeners.add(getClass());
                dispatcher.remove(L1.class);
            }

            @Override
            public Class<A> event() {
                return A.class;
            }
        }

        // L2 will be executed before L1
        dispatcher.add(new L2());
        dispatcher.add(new L1());

        dispatcher.dispatch(new A());
        assertIterableEquals(Arrays.asList(L2.class, L1.class), listeners);

        assertTrue(dispatcher.has(L2.class));
        assertFalse(dispatcher.has(L1.class));

        listeners.clear();

        dispatcher.dispatch(new A());
        assertIterableEquals(Arrays.asList(L2.class), listeners);
    }

    @Test
    void addListenerDuringDispatchShouldBeTakenInAccountAfterDispatch() {
        final Collection<Class> listeners = new ArrayList<>();

        class L1 implements Listener<A> {
            @Override
            public void on(A event) {
                listeners.add(getClass());
            }

            @Override
            public Class<A> event() {
                return A.class;
            }
        }

        class L2 implements Listener<A> {
            @Override
            public void on(A event) {
                listeners.add(getClass());
                dispatcher.add(new L1());
            }

            @Override
            public Class<A> event() {
                return A.class;
            }
        }

        dispatcher.add(new L2());

        dispatcher.dispatch(new A());
        assertIterableEquals(Arrays.asList(L2.class), listeners);

        assertTrue(dispatcher.has(L2.class));
        assertTrue(dispatcher.has(L1.class));

        listeners.clear();

        dispatcher.dispatch(new A());
        assertIterableEquals(Arrays.asList(L2.class, L1.class), listeners);
    }

    @Test
    void addListenerDuringDispatchForOtherEventShouldBeTakenInAccountImmediately() {
        final Collection<Class> listeners = new ArrayList<>();

        class L1 implements Listener<A> {
            @Override
            public void on(A event) {
                listeners.add(getClass());
            }

            @Override
            public Class<A> event() {
                return A.class;
            }
        }

        class L2 implements Listener<B> {
            @Override
            public void on(B event) {
                listeners.add(getClass());
                dispatcher.add(new L1());
                dispatcher.dispatch(new A());
            }

            @Override
            public Class<B> event() {
                return B.class;
            }
        }

        dispatcher.add(new L2());

        dispatcher.dispatch(new B());
        assertIterableEquals(Arrays.asList(L2.class, L1.class), listeners);

        assertTrue(dispatcher.has(L2.class));
        assertTrue(dispatcher.has(L1.class));

        listeners.clear();
        dispatcher.dispatch(new A());
        assertIterableEquals(Arrays.asList(L1.class), listeners);
    }

    @Test
    void dispatchAndRemoveListenerWithSameEventDuringDispatch() {
        final Collection<String> listeners = new ArrayList<>();

        class E
        {
            public final int level;

            public E(int level) {
                this.level = level;
            }
        }

        class L1 implements Listener<E> {
            @Override
            public void on(E event) {
                listeners.add("L1-" + event.level);
            }

            @Override
            public Class<E> event() {
                return E.class;
            }
        }

        class L2 implements Listener<E> {
            @Override
            public void on(E event) {
                listeners.add("L2-" + event.level);

                if (event.level == 0) {
                    dispatcher.dispatch(new E(1));
                    dispatcher.remove(L1.class);
                }
            }

            @Override
            public Class<E> event() {
                return E.class;
            }
        }

        dispatcher.add(new L2());
        dispatcher.add(new L1());

        dispatcher.dispatch(new E(0));
        assertIterableEquals(Arrays.asList("L2-0", "L2-1", "L1-1", "L1-0"), listeners);

        assertTrue(dispatcher.has(L2.class));
        assertFalse(dispatcher.has(L1.class));

        listeners.clear();
        dispatcher.dispatch(new E(0));
        assertIterableEquals(Arrays.asList("L2-0", "L2-1"), listeners);
    }
}
