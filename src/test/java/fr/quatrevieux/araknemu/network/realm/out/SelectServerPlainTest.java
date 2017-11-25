package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectServerPlainTest {
    @Test
    void packet() {
        assertEquals("AYK127.0.0.1:1234;abcd", new SelectServerPlain("127.0.0.1", 1234, "abcd").toString());
    }
}
