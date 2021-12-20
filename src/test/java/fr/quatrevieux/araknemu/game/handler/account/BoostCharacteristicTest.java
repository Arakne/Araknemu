/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.network.game.in.account.AskBoost;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
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
        this.<Player>readField(gamePlayer(), "entity").setBoostPoints(10);
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
    void handleError() throws Exception {
        this.<Player>readField(gamePlayer(), "entity").setBoostPoints(0);
        requestStack.clear();

        assertThrows(ErrorPacket.class, () -> handler.handle(session, new AskBoost(Characteristic.INTELLIGENCE)));
    }

    @Test
    void functionalInActiveFight() throws Exception {
        this.<Player>readField(gamePlayer(), "entity").setBoostPoints(10);

        Fight fight = createFight();
        fight.start();

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new AskBoost(Characteristic.INTELLIGENCE)));
    }

    @Test
    void functionalSuccess() throws Exception {
        this.<Player>readField(gamePlayer(), "entity").setBoostPoints(10);
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
        this.<Player>readField(gamePlayer(), "entity").setBoostPoints(10);

        Fight fight = createFight();

        handlePacket(new AskBoost(Characteristic.INTELLIGENCE));

        assertEquals(151, gamePlayer().properties().characteristics().base().get(Characteristic.INTELLIGENCE));
    }
}
