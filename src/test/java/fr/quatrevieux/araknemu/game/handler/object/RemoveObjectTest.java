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

package fr.quatrevieux.araknemu.game.handler.object;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectDeleteRequest;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;
import fr.quatrevieux.araknemu.network.game.out.object.ItemDeletionError;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RemoveObjectTest extends FightBaseCase {
    private RemoveObject handler;
    private InventoryEntry entry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        GamePlayer player = gamePlayer(true);
        dataSet.pushItemTemplates();

        entry = player.inventory().add(container.get(ItemService.class).create(40), 10);
        requestStack.clear();

        handler = new RemoveObject();
    }

    @Test
    void handleError() throws Exception {
        try {
            handler.handle(session, new ObjectDeleteRequest(45, 10));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertInstanceOf(ItemDeletionError.class, e.packet());
        }
    }

    @Test
    void handleSuccessAllItems() throws Exception {
        requestStack.clear();
        handler.handle(session, new ObjectDeleteRequest(entry.id(), 10));

        requestStack.assertAll(
            new DestroyItem(entry),
            new InventoryWeight(gamePlayer())
        );
    }

    @Test
    void handleSuccessPartial() throws Exception {
        requestStack.clear();
        handler.handle(session, new ObjectDeleteRequest(entry.id(), 3));

        assertEquals(7, entry.quantity());

        requestStack.assertAll(
            new ItemQuantity(entry),
            new InventoryWeight(gamePlayer())
        );
    }

    @Test
    void functionalNotAllowedOnActiveFight() throws Exception {
        Fight fight = createFight();
        fight.start(new AlternateTeamFighterOrder());

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new ObjectDeleteRequest(entry.id(), 10)));
    }

    @Test
    void functionalSuccessDuringPlacement() throws Exception {
        createFight();
        requestStack.clear();

        handlePacket(new ObjectDeleteRequest(entry.id(), 10));

        requestStack.assertAll(
            new DestroyItem(entry),
            new InventoryWeight(gamePlayer())
        );
    }
}
