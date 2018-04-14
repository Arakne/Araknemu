package fr.quatrevieux.araknemu.network.game.in.fight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnEndTest {
    @Test
    void parse() {
        assertNotNull(new TurnEnd.Parser().parse(""));
    }
}