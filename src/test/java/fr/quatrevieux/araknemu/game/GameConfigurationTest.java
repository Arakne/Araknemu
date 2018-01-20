package fr.quatrevieux.araknemu.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameConfigurationTest extends GameBaseCase {
    private GameConfiguration configuration;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        configuration = app.configuration().module(GameConfiguration.class);
    }

    @Test
    void values() {
        assertEquals(2, configuration.id());
        assertEquals(456, configuration.port());
        assertEquals("10.0.0.5", configuration.ip());
    }

    @Test
    void chat() {
        assertEquals(30, configuration.chat().floodTime());
        assertEquals("*#%!pi$:?", configuration.chat().defaultChannels());
        assertEquals("@", configuration.chat().adminChannels());
    }

    @Test
    void player() {
        assertEquals(20, configuration.player().deleteAnswerLevel());
    }
}
