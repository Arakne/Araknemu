package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.network.game.in.account.AskBoost;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoostCharacteristicTest extends FightBaseCase {
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
        gamePlayer().properties().characteristics().setBoostPoints(10);
        requestStack.clear();

        handler.handle(session, new AskBoost(Characteristic.INTELLIGENCE));

        requestStack.assertAll(
            new Stats(gamePlayer().properties()),
            new InventoryWeight(gamePlayer())
        );

        assertEquals(151, gamePlayer().properties().characteristics().base().get(Characteristic.INTELLIGENCE));
        assertEquals(8, gamePlayer().properties().characteristics().boostPoints());
    }

    @Test
    void handleError() throws SQLException, ContainerException {
        gamePlayer().properties().characteristics().setBoostPoints(0);

        assertThrows(IllegalArgumentException.class, () -> handler.handle(session, new AskBoost(Characteristic.INTELLIGENCE)));

        requestStack.assertEmpty();
    }

    @Test
    void functionalInActiveFight() throws Exception {
        player.properties().characteristics().setBoostPoints(10);

        Fight fight = createFight();
        fight.start();

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new AskBoost(Characteristic.INTELLIGENCE)));
    }

    @Test
    void functionalSuccess() throws Exception {
        player.properties().characteristics().setBoostPoints(10);
        requestStack.clear();

        handlePacket(new AskBoost(Characteristic.INTELLIGENCE));

        requestStack.assertAll(
            new Stats(gamePlayer().properties()),
            new InventoryWeight(gamePlayer())
        );

        assertEquals(151, gamePlayer().properties().characteristics().base().get(Characteristic.INTELLIGENCE));
        assertEquals(8, gamePlayer().properties().characteristics().boostPoints());
    }

    @Test
    void functionalSuccessDuringPlacement() throws Exception {
        player.properties().characteristics().setBoostPoints(10);

        Fight fight = createFight();

        handlePacket(new AskBoost(Characteristic.INTELLIGENCE));

        assertEquals(151, gamePlayer().properties().characteristics().base().get(Characteristic.INTELLIGENCE));
    }
}
