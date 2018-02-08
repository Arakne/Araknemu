package fr.quatrevieux.araknemu.network.game.in.object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMoveRequestTest {
    private ObjectMoveRequest.Parser parser = new ObjectMoveRequest.Parser();

    @Test
    void generateWithoutQuantity() {
        ObjectMoveRequest request = parser.parse("5|2");

        assertEquals(5, request.id());
        assertEquals(2, request.position());
        assertEquals(1, request.quantity());
    }

    @Test
    void generateWithQuantity() {
        ObjectMoveRequest request = parser.parse("5|2|52");

        assertEquals(5, request.id());
        assertEquals(2, request.position());
        assertEquals(52, request.quantity());
    }

    @Test
    void generateWithNegativeQuantity() {
        assertThrows(NumberFormatException.class, () -> parser.parse("5|2|-2"));
    }
}