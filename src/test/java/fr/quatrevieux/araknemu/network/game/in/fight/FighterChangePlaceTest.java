package fr.quatrevieux.araknemu.network.game.in.fight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FighterChangePlaceTest {
    @Test
    void parse() {
        assertEquals(123, new FighterChangePlace.Parser().parse("123").cellId());
    }
}