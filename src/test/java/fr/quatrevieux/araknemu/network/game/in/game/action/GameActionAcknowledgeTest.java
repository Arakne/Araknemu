package fr.quatrevieux.araknemu.network.game.in.game.action;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameActionAcknowledgeTest {
    @Test
    void parse() {
        GameActionAcknowledge.Parser parser = new GameActionAcknowledge.Parser();

        assertEquals(123, parser.parse("123").actionId());
    }
}
