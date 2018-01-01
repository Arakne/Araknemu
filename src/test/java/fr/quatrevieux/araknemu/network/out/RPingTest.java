package fr.quatrevieux.araknemu.network.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RPingTest {
    @Test
    void generate() {
        assertEquals("rpingMY_PAYLOAD", new RPing("MY_PAYLOAD").toString());
    }
}