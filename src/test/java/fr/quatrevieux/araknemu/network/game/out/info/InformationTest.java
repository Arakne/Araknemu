package fr.quatrevieux.araknemu.network.game.out.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InformationTest {
    @Test
    void chatFlood() {
        assertEquals(
            "Im0115;1234",
            Information.chatFlood(1234).toString()
        );
    }
}
