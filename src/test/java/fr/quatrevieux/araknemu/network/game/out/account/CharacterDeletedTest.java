package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterDeletedTest {
    @Test
    void success() {
        assertEquals("ADK", new CharacterDeleted(true).toString());
    }

    @Test
    void error() {
        assertEquals("ADE", new CharacterDeleted(false).toString());
    }
}
