package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExchangeRequestedTest extends GameBaseCase {
    @Test
    void generate() throws Exception {
        assertEquals(
            "ERK1|2|1",
            new ExchangeRequested(explorationPlayer(), makeOtherExplorationPlayer(), ExchangeType.PLAYER_EXCHANGE).toString()
        );
    }
}
