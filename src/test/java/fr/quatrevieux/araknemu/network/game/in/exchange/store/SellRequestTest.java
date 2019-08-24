package fr.quatrevieux.araknemu.network.game.in.exchange.store;

import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SellRequestTest {
    private SellRequest.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new SellRequest.Parser();
    }

    @Test
    void parseSuccess() {
        SellRequest request = parser.parse("112|3");

        assertEquals(112, request.itemId());
        assertEquals(3, request.quantity());
    }

    @Test
    void parseInvalid() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }

    @Test
    void parseNegativeQuantity() {
        assertThrows(NumberFormatException.class, () -> parser.parse("112|-3"));
    }
}
