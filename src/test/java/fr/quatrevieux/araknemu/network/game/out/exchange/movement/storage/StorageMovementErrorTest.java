package fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageMovementErrorTest {
    @Test
    void generate() {
        assertEquals("EsE", new StorageMovementError().toString());
    }
}
