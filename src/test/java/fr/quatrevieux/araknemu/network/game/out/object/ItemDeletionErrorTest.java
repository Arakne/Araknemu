package fr.quatrevieux.araknemu.network.game.out.object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemDeletionErrorTest {
    @Test
    void generate() {
        assertEquals("ODE", new ItemDeletionError().toString());
    }
}
