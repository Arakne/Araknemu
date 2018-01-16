package fr.quatrevieux.araknemu.network.game.out.info;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {
    @Test
    void welcome() {
        assertEquals("Im189;", Error.welcome().toString());
    }

    @Test
    void cantDoOnServer() {
        assertEquals("Im1226;", Error.cantDoOnServer().toString());
    }
}
