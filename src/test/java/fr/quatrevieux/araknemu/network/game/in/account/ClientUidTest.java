package fr.quatrevieux.araknemu.network.game.in.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientUidTest {
    @Test
    void parse() {
        ClientUid.Parser parser = new ClientUid.Parser();

        assertEquals("abcd", parser.parse("abcd").uid());
    }
}