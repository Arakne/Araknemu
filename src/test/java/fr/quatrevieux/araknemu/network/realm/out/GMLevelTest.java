package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GMLevelTest {
    @Test
    void test_toString() {
        assertEquals("AlK1", new GMLevel(true).toString());
        assertEquals("AlK0", new GMLevel(false).toString());
    }
}
