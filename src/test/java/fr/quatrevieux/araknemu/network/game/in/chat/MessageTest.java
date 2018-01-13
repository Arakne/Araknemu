package fr.quatrevieux.araknemu.network.game.in.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    private Message.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Message.Parser();
    }

    @Test
    void parseInvalid() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }

    @Test
    void parseWithGlobalChannel() {
        Message message = parser.parse("*|My message|my items");

        assertEquals(ChannelType.MESSAGES, message.channel());
        assertEquals("My message", message.message());
        assertNull(message.target());
        assertEquals("my items", message.items());
    }

    @Test
    void parseWisp() {
        Message message = parser.parse("Bob|My message|my items");

        assertEquals(ChannelType.PRIVATE, message.channel());
        assertEquals("My message", message.message());
        assertEquals("Bob", message.target());
        assertEquals("my items", message.items());
    }

    @Test
    void parseWithoutItem() {
        Message message = parser.parse("Bob|My message|");

        assertEquals(ChannelType.PRIVATE, message.channel());
        assertEquals("My message", message.message());
        assertEquals("Bob", message.target());
        assertEquals("", message.items());
    }
}
