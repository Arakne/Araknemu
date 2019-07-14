package fr.quatrevieux.araknemu.network.game.in.exchange.movement;

import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemsMovementTest {
    private ItemsMovement.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new ItemsMovement.Parser();
    }

    @Test
    void add() {
        ItemsMovement packet = parser.parse("+12|3");

        assertEquals(12, packet.id());
        assertEquals(3, packet.quantity());
        assertEquals(0, packet.price());
    }

    @Test
    void remove() {
        ItemsMovement packet = parser.parse("-12|3");

        assertEquals(12, packet.id());
        assertEquals(-3, packet.quantity());
        assertEquals(0, packet.price());
    }

    @Test
    void withPrice() {
        ItemsMovement packet = parser.parse("+12|1|5");

        assertEquals(12, packet.id());
        assertEquals(1, packet.quantity());
        assertEquals(5, packet.price());
    }

    @Test
    void invalid() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }
}
