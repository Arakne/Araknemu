package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewPlayerLevelTest {
    @Test
    void generate() {
        assertEquals("AN123", new NewPlayerLevel(123).toString());
    }
}