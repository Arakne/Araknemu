package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterSelectionErrorTest {
    @Test
    void generate() {
        assertEquals("ASE", new CharacterSelectionError().toString());
    }
}
