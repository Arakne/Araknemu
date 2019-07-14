package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeCreatedTest extends GameBaseCase {
    @Test
    void withoutTarget() {
        assertEquals("ECK1", new ExchangeCreated(ExchangeType.PLAYER_EXCHANGE).toString());
    }

    @Test
    void withTarget() throws SQLException {
        assertEquals("ECK1|1", new ExchangeCreated(ExchangeType.PLAYER_EXCHANGE, explorationPlayer()).toString());
    }
}
