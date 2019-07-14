package fr.quatrevieux.araknemu.network.game.out.exchange;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeLeavedTest {
    @Test
    void cancelled() {
        assertEquals("EV", ExchangeLeaved.cancelled().toString());
    }

    @Test
    void accepted() {
        assertEquals("EVa", ExchangeLeaved.accepted().toString());
    }
}
