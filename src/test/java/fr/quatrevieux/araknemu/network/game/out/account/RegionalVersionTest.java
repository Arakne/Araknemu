package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionalVersionTest {
    @Test
    void generate() {
        assertEquals("AV0", new RegionalVersion(0).toString());
    }
}