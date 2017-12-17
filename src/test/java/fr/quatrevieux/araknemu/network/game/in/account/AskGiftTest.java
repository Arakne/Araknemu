package fr.quatrevieux.araknemu.network.game.in.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskGiftTest {
    @Test
    void parse() {
        AskGift.Parser parser = new AskGift.Parser();

        assertEquals("fr", parser.parse("fr").language());
    }
}