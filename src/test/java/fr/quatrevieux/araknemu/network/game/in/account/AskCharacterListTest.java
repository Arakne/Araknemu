package fr.quatrevieux.araknemu.network.game.in.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskCharacterListTest {
    @Test
    void parse() {
        AskCharacterList.Parser parser = new AskCharacterList.Parser();

        assertTrue(parser.parse("f").forced());
        assertFalse(parser.parse("").forced());
    }
}
