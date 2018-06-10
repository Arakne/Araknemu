package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaveFightRequestTest extends TestCase {
    @Test
    void parse() {
        assertInstanceOf(LeaveFightRequest.class, new LeaveFightRequest.Parser().parse(""));
    }
}