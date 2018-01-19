package fr.quatrevieux.araknemu.network.game.in.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeleteCharacterRequestTest {
    final private DeleteCharacterRequest.Parser parser = new DeleteCharacterRequest.Parser();

    @Test
    void parseWithAnswer() {
        DeleteCharacterRequest packet = parser.parse("123|My answer");

        assertEquals(123, packet.id());
        assertEquals("My answer", packet.answer());
    }

    @Test
    void parseWithoutAnswer() {
        DeleteCharacterRequest packet = parser.parse("123|");

        assertEquals(123, packet.id());
        assertEquals("", packet.answer());
    }
}
