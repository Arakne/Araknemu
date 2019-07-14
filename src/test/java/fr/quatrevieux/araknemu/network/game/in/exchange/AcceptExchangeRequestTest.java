package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

class AcceptExchangeRequestTest extends TestCase {
    @Test
    void parse() {
        assertInstanceOf(AcceptExchangeRequest.class, new AcceptExchangeRequest.Parser().parse(""));
    }
}
