package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChooseServerTest {
    @Test
    void parseSuccess() {
        ChooseServer.Parser parser = new ChooseServer.Parser();

        ChooseServer p = parser.parse("123");

        assertEquals(123, p.id());
    }

    @Test
    void parseInvalidNumber() {
        ChooseServer.Parser parser = new ChooseServer.Parser();
        assertThrows(ParsePacketException.class, () -> parser.parse("NaN"));
    }
}