package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommunityTest {
    @Test
    void test_toString() {
        assertEquals("Ac5", new Community(5).toString());
    }
}