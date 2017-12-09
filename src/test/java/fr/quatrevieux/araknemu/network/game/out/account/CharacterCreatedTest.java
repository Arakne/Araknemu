package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCreatedTest {
    @Test
    void generate() {
        assertEquals("AAK", new CharacterCreated().toString());
    }
}