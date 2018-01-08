package fr.quatrevieux.araknemu.network.game.in.game.action;

import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameActionCancelTest {
    @Test
    void parse() {
        GameActionCancel.Parser parser = new GameActionCancel.Parser();

        GameActionCancel packet = parser.parse("123|azerty");

        assertEquals(123, packet.actionId());
        assertEquals("azerty", packet.argument());
    }

    @Test
    void parseBadFormat() {
        assertThrows(ParsePacketException.class, () -> new GameActionCancel.Parser().parse("invalid"), "GKEinvalid : The packet should have 2 parts separated by a pipe");
    }
}
