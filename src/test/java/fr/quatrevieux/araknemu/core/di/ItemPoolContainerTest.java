package fr.quatrevieux.araknemu.core.di;

import fr.quatrevieux.araknemu.realm.ConnectionKey;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemPoolContainerTest {
    private ItemPoolContainer container;

    @BeforeEach
    void setUp() {
        container = new ItemPoolContainer();
    }

    @Test
    void has() {
        assertFalse(container.has(RandomUtils.class));

        ItemPoolContainer.Configurator configurator = container.configurator();
        configurator.set(new RandomUtils());

        assertTrue(container.has(RandomUtils.class));
    }

    @Test
    void getNotFound() {
        assertThrows(ItemNotFoundException.class, () -> container.get(RandomUtils.class));
    }

    @Test
    void getSuccess() throws ContainerException {
        RandomUtils ru = new RandomUtils();
        ItemPoolContainer.Configurator configurator = container.configurator();
        configurator.set(ru);

        assertSame(ru, container.get(RandomUtils.class));
    }

    @Test
    void configuratorSet() throws ContainerException {
        ItemPoolContainer.Configurator configurator = container.configurator();

        assertSame(configurator, configurator.set(Object.class, new RandomUtils()));
        assertTrue(container.get(Object.class) instanceof RandomUtils);
    }

    @Test
    void configuratorFactory() throws ContainerException {
        ItemPoolContainer.Configurator configurator = container.configurator();

        assertSame(configurator, configurator.factory(ConnectionKey.class, (container) -> new ConnectionKey("123")));
        assertTrue(container.get(ConnectionKey.class) instanceof ConnectionKey);
        assertEquals("123", container.get(ConnectionKey.class).key());
        assertNotSame(container.get(ConnectionKey.class), container.get(ConnectionKey.class));
    }

    @Test
    void configuratorPersist() throws ContainerException {
        ItemPoolContainer.Configurator configurator = container.configurator();

        assertSame(configurator, configurator.persist(ConnectionKey.class, (container) -> new ConnectionKey("123")));
        assertTrue(container.get(ConnectionKey.class) instanceof ConnectionKey);
        assertEquals("123", container.get(ConnectionKey.class).key());
        assertSame(container.get(ConnectionKey.class), container.get(ConnectionKey.class));
    }

    static public class A {}
    static public class B {
        public A a;
        public B(A a) { this.a = a; }
    }
    public interface I {}
    static public class C implements I {}

    static public class Module implements ContainerModule {
        @Override
        public void configure(ContainerConfigurator configurator) {
            configurator
                .set(new A())
                .factory(I.class, c -> new C())
                .persist(B.class, c -> new B(c.get(A.class)))
            ;
        }
    }

    @Test
    void register() throws ContainerException {
        container.register(new Module());

        assertTrue(container.get(I.class) instanceof C);
        assertNotSame(container.get(I.class), container.get(I.class));
        assertSame(container.get(A.class), container.get(A.class));
        assertSame(container.get(A.class), container.get(B.class).a);
    }
}
