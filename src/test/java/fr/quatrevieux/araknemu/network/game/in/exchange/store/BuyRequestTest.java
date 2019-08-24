package fr.quatrevieux.araknemu.network.game.in.exchange.store;

import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuyRequestTest {
    private BuyRequest.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new BuyRequest.Parser();
    }

    @Test
    void parseSuccess() {
        BuyRequest request = parser.parse("12|3");

        assertEquals(12, request.itemId());
        assertEquals(3, request.quantity());
    }

    @Test
    void parseInvalidFormat() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }
}
