package fr.quatrevieux.araknemu.network.game.out.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTokenSuccessTest {
    @Test
    void generate() {
        assertEquals("ATKC123", new LoginTokenSuccess(12, "123").toString());
        assertEquals("ATK0", new LoginTokenSuccess().toString());
    }
}