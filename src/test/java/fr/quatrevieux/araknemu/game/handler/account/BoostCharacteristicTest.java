package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.in.account.AskBoost;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class BoostCharacteristicTest extends GameBaseCase {
    private BoostCharacteristic handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new BoostCharacteristic();
        gamePlayer(true);

        requestStack.clear();
    }

    @Test
    void handleSuccess() throws Exception {
        gamePlayer().characteristics().setBoostPoints(10);

        handler.handle(session, new AskBoost(Characteristic.INTELLIGENCE));

        requestStack.assertLast(
            new Stats(gamePlayer())
        );

        assertEquals(151, gamePlayer().characteristics().base().get(Characteristic.INTELLIGENCE));
        assertEquals(8, gamePlayer().characteristics().boostPoints());
    }

    @Test
    void handleError() throws SQLException, ContainerException {
        gamePlayer().characteristics().setBoostPoints(0);

        assertThrows(IllegalArgumentException.class, () -> handler.handle(session, new AskBoost(Characteristic.INTELLIGENCE)));

        requestStack.assertEmpty();
    }
}
