package fr.quatrevieux.araknemu.network.game.in.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskRandomNameTest {
    @Test
    void parser() {
        AskRandomName.Parser parser = new AskRandomName.Parser();

        assertNotNull(parser.parse(""));
    }
}
