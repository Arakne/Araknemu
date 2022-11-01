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

package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UseObjectBeforeStartTest extends FightBaseCase {
    private UseObjectBeforeStart handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new UseObjectBeforeStart();

        dataSet.pushUsableItems();
        requestStack.clear();
    }

    @Test
    void handleDoNothing() throws Exception {
        createFight();

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(new Noop());
        assertEquals(1, entry.quantity());    }

    @Test
    void handleSuccess() throws Exception {
        player.properties().life().set(10);

        createFight();

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(
            new Stats(player.properties()),
            Information.heal(10),
            new DestroyItem(entry),
            new InventoryWeight(player)
        );

        assertEquals(20, player.properties().life().current());
        assertEquals(20, player.fighter().life().current());
        assertEquals(0, entry.quantity());
    }

    @Test
    void functionalErrorOnActiveFight() throws Exception {
        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));

        Fight fight = createFight();
        fight.start(new AlternateTeamFighterOrder());

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new ObjectUseRequest(entry.id(), 0, 0, false)));
    }

    @Test
    void functionalSuccess() throws Exception {
        player.properties().life().set(10);

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(468));

        createFight();

        handlePacket(new ObjectUseRequest(entry.id(), 0, 0, false));
        assertEquals(20, player.fighter().life().current());
    }
}
