package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadVersionTest {
    @Test
    void test_toString() {
        assertEquals("AlEv1.29", new BadVersion("1.29").toString());
    }
}