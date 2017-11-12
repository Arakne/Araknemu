package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RealmConfigurationTest {
    private RealmConfiguration configuration;

    @BeforeEach
    void setUp() throws IOException {
        configuration = new RealmConfiguration();
        configuration.setPool(
            new IniDriver(
                new Ini(new File("src/test/test_config.ini"))
            ).pool("realm")
        );
    }

    @Test
    void port() {
        assertEquals(456, configuration.port());
    }

    @Test
    void clientVersion() {
        assertEquals("1.29.1", configuration.clientVersion());
    }
}
