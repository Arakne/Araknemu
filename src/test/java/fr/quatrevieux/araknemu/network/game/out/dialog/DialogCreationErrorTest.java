package fr.quatrevieux.araknemu.network.game.out.dialog;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DialogCreationErrorTest {
    @Test
    void generate() {
        assertEquals("DCE", new DialogCreationError().toString());
    }
}