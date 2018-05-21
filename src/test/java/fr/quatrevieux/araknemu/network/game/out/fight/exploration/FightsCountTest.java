package fr.quatrevieux.araknemu.network.game.out.fight.exploration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FightsCountTest {
    @Test
    void generate() {
        assertEquals("fC5", new FightsCount(5).toString());
    }
}