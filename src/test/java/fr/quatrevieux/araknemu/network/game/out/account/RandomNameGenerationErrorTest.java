package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomNameGenerationErrorTest {
    @Test
    void generate() {
        assertEquals("APE1", new RandomNameGenerationError(RandomNameGenerationError.Error.UNDEFINED).toString());
    }
}