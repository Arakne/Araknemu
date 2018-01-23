package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomNameGeneratedTest {
    @Test
    void generate() {
        assertEquals(
            "APKBob",
            new RandomNameGenerated("Bob").toString()
        );
    }
}
