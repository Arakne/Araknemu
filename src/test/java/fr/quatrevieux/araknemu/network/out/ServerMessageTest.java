package fr.quatrevieux.araknemu.network.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerMessageTest {
    @Test
    void displayNow() {
        assertEquals("M112|1;2", new ServerMessage(true, 12, new Object[] {1, 2}, null).toString());
    }

    @Test
    void displayOnLogout() {
        assertEquals("M012|1;2", new ServerMessage(false, 12, new Object[] {1, 2}, null).toString());
    }

    @Test
    void withName() {
        assertEquals("M112|1;2|name", new ServerMessage(true, 12, new Object[] {1, 2}, "name").toString());
    }

    @Test
    void notEnoughKamasForBank() {
        assertEquals("M110|123", ServerMessage.notEnoughKamasForBank(123).toString());
    }
}
