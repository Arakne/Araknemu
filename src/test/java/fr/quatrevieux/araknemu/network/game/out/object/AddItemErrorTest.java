package fr.quatrevieux.araknemu.network.game.out.object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddItemErrorTest {
    @Test
    void generate() {
        assertEquals(
            "OAEL",
            new AddItemError(AddItemError.Error.TOO_LOW_LEVEL).toString()
        );
    }
}