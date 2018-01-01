package fr.quatrevieux.araknemu.network.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueuePositionTest {
    @Test
    void generate() {
        assertEquals("Aq5", new QueuePosition(5).toString());
    }
}