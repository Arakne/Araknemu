package fr.quatrevieux.araknemu.network.game.in.fight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FighterReadyTest {
    @Test
    void parse() {
        assertTrue(new FighterReady.Parser().parse("1").ready());
        assertFalse(new FighterReady.Parser().parse("0").ready());
    }
}
