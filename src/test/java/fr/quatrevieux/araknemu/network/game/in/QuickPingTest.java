package fr.quatrevieux.araknemu.network.game.in;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuickPingTest extends TestCase {
    @Test
    void parse() {
        assertInstanceOf(QuickPing.class, new QuickPing.Parser().parse(""));
    }
}