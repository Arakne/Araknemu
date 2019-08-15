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
        assertEquals(1, configuration.bankCostPerEntry());
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
        assertEquals(4, configuration.player().minNameGeneratedLength());
        assertEquals(8, configuration.player().maxNameGeneratedLength());
        assertEquals(1, configuration.player().spellBoostPointsOnLevelUp());
        assertEquals(5, configuration.player().characteristicPointsOnLevelUp());
    }

    @Test
    void activity() {
        assertEquals(1, configuration.activity().threadsCount());
        assertEquals(120, configuration.activity().monsterMoveInterval());
        assertEquals(25, configuration.activity().monsterMovePercent());
    }
}
