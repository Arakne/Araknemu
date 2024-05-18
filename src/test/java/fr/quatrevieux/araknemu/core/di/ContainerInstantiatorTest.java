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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContainerInstantiatorTest {
    private Container container;
    private ContainerInstantiator instantiator;

    @BeforeEach
    void setUp() {
        container = new ItemPoolContainer();
        instantiator = new ContainerInstantiator(container);
    }

    @Test
    void cantInstantiateJavaClasses() {
        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(String.class));
        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(Long.class));
        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(int[].class));
        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(Map.class));
        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(int.class));
    }

    @Test
    void cantInstantiateOnlyPrivateConstructor() {
        class A {
            private A(){}
        }

        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(A.class));
    }

    @Test
    void cantInstantiateInterface() {
        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(I.class));
    }

    @Test
    void cantInstantiateAbstractClass() {
        abstract class B {
            public B() {}
        }

        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(B.class));
    }

    @Test
    void cantInstantiateIfDependencyCannotBeInstantiated() {
        class V {
            public V(int i) {}
        }

        class B {
            public B(V v) {}
        }

        assertThrows(InstantiatorException.class, () -> instantiator.instantiate(B.class));
    }

    @Test
    void instantiateAlreadyDeclaredShouldReturnInstanceFromContainer() {
        A a = new A();

        container.register(c -> c.set(a));

        assertSame(a, instantiator.instantiate(A.class));
    }

    @Test
    void instantiateShouldAutowireDependencies() {
        class B {
            private A a;

            public B(A a) {
                this.a = a;
            }
        }

        A a = new A();
        container.register(c -> c.set(a));

        B b = instantiator.instantiate(B.class);
        assertSame(a, b.a);
    }

    @Test
    void instantiateShouldRecursivelyInstantiateDependencies() {
        class B {
            private A a;

            public B(A a) {
                this.a = a;
            }
        }

        class C {
            private B b;

            public C(B b) {
                this.b = b;
            }
        }

        A a = new A();
        container.register(c -> c.set(a));

        C c = instantiator.instantiate(C.class);
        assertSame(a, c.b.a);
        assertNotSame(instantiator.instantiate(C.class), c);
    }

    @Test
    void instantiateShouldSelectTheFirstLegalConstructor() {
        class V {
            private int i;

            public V(int i) {
                this.i = i;
            }
        }

        class VProvider {
            public V provider() {
                return new V(42);
            }
        }

        class Foo {
            private V v;

            private Foo(V v) {
                this.v = v;
            }

            public Foo(int v) {
                this(new V(v));
            }

            public Foo(VProvider provider) {
                this(provider.provider());
            }
        }

        container.register(c -> {
            c.set(new VProvider());
            c.set(new V(1));
        });

        Foo foo = instantiator.instantiate(Foo.class);
        assertEquals(42, foo.v.i);
    }

    interface I {}

    class A {}
}
