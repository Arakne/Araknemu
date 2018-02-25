package fr.quatrevieux.araknemu.network.game.in.object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectUseRequestTest {
    @Test
    void parseSelfUsage() {
        ObjectUseRequest request = new ObjectUseRequest.Parser().parse("123");

        assertEquals(123, request.objectId());
        assertFalse(request.isTarget());
    }

    @Test
    void parseWithTarget() {
        ObjectUseRequest request = new ObjectUseRequest.Parser().parse("123|45|325");

        assertEquals(123, request.objectId());
        assertEquals(45, request.target());
        assertEquals(325, request.cell());
        assertTrue(request.isTarget());
    }

    @Test
    void parseWithCell() {
        ObjectUseRequest request = new ObjectUseRequest.Parser().parse("123||325");

        assertEquals(123, request.objectId());
        assertEquals(0, request.target());
        assertEquals(325, request.cell());
        assertTrue(request.isTarget());
    }
}