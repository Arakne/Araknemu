package fr.quatrevieux.araknemu.network.game.in.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskDateTest {
    @Test
    void parser() {
        AskDate.Parser parser = new AskDate.Parser();

        assertNotNull(parser.parse(""));
    }
}
