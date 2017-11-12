package fr.quatrevieux.araknemu.core.config;

import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DefaultConfigurationTest {
    static public class FooModule implements ConfigurationModule {
        public Pool pool;

        @Override
        public void setPool(Pool pool) {
            this.pool = pool;
        }

        @Override
        public String name() {
            return "foo";
        }
    }

    static public class EmptyModule implements ConfigurationModule {
        public Pool pool;

        @Override
        public void setPool(Pool pool) {
            this.pool = pool;
        }

        @Override
        public String name() {
            return "empty";
        }
    }

    private DefaultConfiguration configuration;

    @BeforeEach
    void setUp() throws IOException {
        configuration = new DefaultConfiguration(
            new IniDriver(
                new Ini(new File("src/test/test_config.ini"))
            )
        );
    }

    @Test
    void moduleOnFirstCall() {
        FooModule module = configuration.module(FooModule.class);

        assertTrue(module.pool instanceof IniPool);
        assertEquals("baz", module.pool.get("bar"));
    }

    @Test
    void moduleSameInstance() {
        assertSame(configuration.module(FooModule.class), configuration.module(FooModule.class));
    }

    @Test
    void moduleNotFound() {
        EmptyModule module = configuration.module(EmptyModule.class);

        assertTrue(module.pool instanceof EmptyPool);
    }
}
