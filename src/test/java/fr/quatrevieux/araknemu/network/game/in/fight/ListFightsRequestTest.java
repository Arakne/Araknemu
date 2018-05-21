package fr.quatrevieux.araknemu.network.game.in.fight;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListFightsRequestTest extends TestCase {
    @Test
    void parse() {
        assertInstanceOf(ListFightsRequest.class, new ListFightsRequest.Parser().parse(""));
    }
}
