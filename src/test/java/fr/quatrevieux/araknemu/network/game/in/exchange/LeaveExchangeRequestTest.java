package fr.quatrevieux.araknemu.network.game.in.exchange;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

class LeaveExchangeRequestTest extends TestCase {
    @Test
    void parse() {
        assertInstanceOf(LeaveExchangeRequest.class, new LeaveExchangeRequest.Parser().parse(""));
    }
}
