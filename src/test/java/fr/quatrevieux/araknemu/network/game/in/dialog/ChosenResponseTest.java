package fr.quatrevieux.araknemu.network.game.in.dialog;

import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChosenResponseTest {
    private ChosenResponse.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new ChosenResponse.Parser();
    }

    @Test
    void parseError() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }

    @Test
    void parse() {
        ChosenResponse response = parser.parse("123|456");

        assertEquals(123, response.question());
        assertEquals(456, response.response());
    }
}
