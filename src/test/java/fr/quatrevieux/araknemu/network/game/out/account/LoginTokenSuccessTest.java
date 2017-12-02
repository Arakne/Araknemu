package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTokenSuccessTest {
    @Test
    void generate() {
        assertEquals("ATK0", new LoginTokenSuccess(0).toString());
    }
}