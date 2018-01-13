package fr.quatrevieux.araknemu.network.game.in;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PingTest {
    @Test
    void parser() {
        Ping.Parser parser = new Ping.Parser();

        assertNotNull(parser.parse(""));
    }
}