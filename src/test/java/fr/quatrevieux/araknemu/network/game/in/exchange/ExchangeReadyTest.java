package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

class ExchangeReadyTest extends TestCase {
    @Test
    void parse() {
        assertInstanceOf(ExchangeReady.class, new ExchangeReady.Parser().parse(""));
    }
}
