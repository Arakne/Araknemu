package fr.quatrevieux.araknemu.network.game.in.fight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskFightDetailsTest {
    @Test
    void parse() {
        assertEquals(123, new AskFightDetails.Parser().parse("123").fightId());
    }
}