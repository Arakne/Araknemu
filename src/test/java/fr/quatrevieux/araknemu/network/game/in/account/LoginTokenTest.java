package fr.quatrevieux.araknemu.network.game.in.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTokenTest {
    @Test
    void parse() {
        LoginToken.Parser parser = new LoginToken.Parser();
        LoginToken token = parser.parse("123");
        assertEquals("123", token.token());
    }
}
