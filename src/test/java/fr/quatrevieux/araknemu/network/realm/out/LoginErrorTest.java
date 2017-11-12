package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginErrorTest {
    @Test
    void test_toString() {
        assertEquals("AlEc", new LoginError(LoginError.ALREADY_LOGGED_GAME_SERVER).toString());
    }

}