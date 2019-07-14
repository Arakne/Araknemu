package fr.quatrevieux.araknemu.network.game.out.exchange;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRequestErrorTest {
    @Test
    void generate() {
        assertEquals("EREO", new ExchangeRequestError(ExchangeRequestError.Error.ALREADY_EXCHANGE).toString());
    }
}
