package fr.quatrevieux.araknemu.network.game.in.game.action;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameActionRequestTest {
    @Test
    void parse() {
        GameActionRequest.Parser parser = new GameActionRequest.Parser();

        GameActionRequest ga = parser.parse("001dfi");

        assertEquals(1, ga.type());
        assertArrayEquals(new String[] {"dfi"}, ga.arguments());
    }
}
