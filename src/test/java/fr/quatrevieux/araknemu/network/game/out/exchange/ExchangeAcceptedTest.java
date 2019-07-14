package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeAcceptedTest extends GameBaseCase {
    @Test
    void generate() throws SQLException {
        assertEquals(
            "EK11",
            new ExchangeAccepted(true, explorationPlayer()).toString()
        );
    }
}
