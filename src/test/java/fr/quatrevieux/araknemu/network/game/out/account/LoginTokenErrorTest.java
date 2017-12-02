package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTokenErrorTest {
    @Test
    void generate() {
        assertEquals("ATE", new LoginTokenError().toString());
    }
}