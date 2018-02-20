package fr.quatrevieux.araknemu.network.game.in.object;

import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectDeleteRequestTest {
    @Test
    void parse() {
        ObjectDeleteRequest request = new ObjectDeleteRequest.Parser().parse("12|5");

        assertEquals(12, request.id());
        assertEquals(5, request.quantity());
    }

    @Test
    void parseBadFormat() {
        assertThrows(ParsePacketException.class, () -> new ObjectDeleteRequest.Parser().parse("invalid"), "Needs 2 parts");
    }

    @Test
    void parseNegativeQuantity() {
        assertThrows(NumberFormatException.class, () -> new ObjectDeleteRequest.Parser().parse("12|-5"));
    }
}
