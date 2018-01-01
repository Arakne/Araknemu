package fr.quatrevieux.araknemu.network.game.in.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskExtraInfoTest {
    @Test
    void parser() {
        assertNotNull(new AskExtraInfo.Parser().parse(""));
    }
}