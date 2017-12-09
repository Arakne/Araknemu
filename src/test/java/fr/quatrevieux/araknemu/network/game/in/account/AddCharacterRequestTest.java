package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddCharacterRequestTest {
    @Test
    void parseSuccess() {
        AddCharacterRequest.Parser parser = new AddCharacterRequest.Parser();

        AddCharacterRequest request = parser.parse("bob|5|1|145|541|123");

        assertEquals("bob", request.name());
        assertEquals(Race.XELOR, request.race());
        assertEquals(Sex.FEMALE, request.sex());
        assertEquals(145, request.colors().color1());
        assertEquals(541, request.colors().color2());
        assertEquals(123, request.colors().color3());
    }

    @Test
    void parseInvalidData() {
        AddCharacterRequest.Parser parser = new AddCharacterRequest.Parser();

        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }

    @Test
    void parseInvalidRace() {
        AddCharacterRequest.Parser parser = new AddCharacterRequest.Parser();

        assertThrows(IndexOutOfBoundsException.class, () -> parser.parse("bob|0|1|145|541|123"));
    }
}
