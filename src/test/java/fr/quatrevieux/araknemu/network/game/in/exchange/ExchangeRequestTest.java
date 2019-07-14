package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRequestTest {
    private ExchangeRequest.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new ExchangeRequest.Parser();
    }

    @Test
    void parseBadPartsNumber() {
        assertThrows(ParsePacketException.class, () -> parser.parse("2"));
    }

    @Test
    void parseBadType() {
        assertThrows(ParsePacketException.class, () -> parser.parse("33|123"));
    }

    @Test
    void parseWithId() {
        ExchangeRequest request = parser.parse("2|123");

        assertEquals(ExchangeType.NPC_EXCHANGE, request.type());
        assertEquals(123, request.id().get().intValue());
        assertFalse(request.cell().isPresent());
    }

    @Test
    void parseWithIdAndCell() {
        ExchangeRequest request = parser.parse("2|123|145");

        assertEquals(ExchangeType.NPC_EXCHANGE, request.type());
        assertEquals(123, request.id().get().intValue());
        assertEquals(145, request.cell().get().intValue());
    }

    @Test
    void parseWithCell() {
        ExchangeRequest request = parser.parse("2||145");

        assertEquals(ExchangeType.NPC_EXCHANGE, request.type());
        assertFalse(request.id().isPresent());
        assertEquals(145, request.cell().get().intValue());
    }
}
