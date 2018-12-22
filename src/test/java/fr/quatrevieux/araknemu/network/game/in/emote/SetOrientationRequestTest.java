package fr.quatrevieux.araknemu.network.game.in.emote;

import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetOrientationRequestTest {
    private SetOrientationRequest.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new SetOrientationRequest.Parser();
    }

    @Test
    void invalidDirection() {
        assertThrows(ParsePacketException.class, () -> parser.parse("9"));
    }

    @Test
    void parse() {
        assertEquals(Direction.SOUTH_EAST, parser.parse("1").orientation());
        assertEquals(Direction.WEST, parser.parse("4").orientation());
    }
}
