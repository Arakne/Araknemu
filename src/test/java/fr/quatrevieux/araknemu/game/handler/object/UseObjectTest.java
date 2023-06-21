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

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.in.object.ObjectUseRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UseObjectTest extends GameBaseCase {
    private UseObject handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new UseObject();

        dataSet.pushUsableItems();
        gamePlayer(true);

        explorationPlayer();
        requestStack.clear();
    }

    @Test
    void handleForSelfSuccessWithBoostStatsObject() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(800));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(
            new Stats(gamePlayer().properties()),
            new InventoryWeight(gamePlayer()),
            Information.characteristicBoosted(Characteristic.AGILITY, 1),
            new DestroyItem(entry),
            new InventoryWeight(gamePlayer())
        );

        assertThrows(ItemNotFoundException.class, () -> explorationPlayer().inventory().get(entry.id()));
        assertEquals(0, entry.quantity());
        assertEquals(1, explorationPlayer().properties().characteristics().base().get(Characteristic.AGILITY));
    }

    @Test
    void handleDoNothing() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(468));
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false));

        requestStack.assertAll(new Noop());
    }

    @Test
    void handleForTargetNotOnMapShouldDoNothing() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(468));

        GamePlayer other = makeOtherPlayer();
        ExplorationPlayer otherPlayer = new ExplorationPlayer(other);
        otherPlayer.changeMap(explorationPlayer().map(), 123);
        other.properties().life().set(10);
        explorationPlayer().leave();
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), otherPlayer.id(), -1, true));

        requestStack.assertAll(new Noop());
        assertEquals(10, other.properties().life().current());
        assertEquals(1, entry.quantity());
    }

    @Test
    void handleForTargetPlayer() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(468));

        GamePlayer other = makeOtherPlayer();
        ExplorationPlayer otherPlayer = new ExplorationPlayer(other);
        otherPlayer.changeMap(explorationPlayer().map(), 123);
        other.properties().life().set(10);
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), otherPlayer.id(), -1, true));

        requestStack.assertAll(
            new DestroyItem(entry),
            new InventoryWeight(gamePlayer())
        );
        assertEquals(20, other.properties().life().current());
        assertEquals(0, entry.quantity());
    }

    @Test
    void handleForTargetCell() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(2240), 100);
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 150, true));

        requestStack.assertAll(
            new GameActionResponse("1", ActionType.FIREWORK, explorationPlayer().id(), "150,2900,11,8,1"),
            new ItemQuantity(entry),
            new InventoryWeight(gamePlayer())
        );
        assertEquals(99, entry.quantity());
    }

    @Test
    void handleForTargetInvalidCellShouldDoNothing() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(2240), 100);
        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), 0, 1000, true));

        requestStack.assertAll(new Noop());
        assertEquals(100, entry.quantity());
    }

    @Test
    void handleForTargetNpc() throws Exception {
        dataSet.pushNpcs();

        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(468));

        GameNpc npc = container.get(NpcService.class).get(457);
        npc.join(explorationPlayer().map());

        requestStack.clear();

        handler.handle(session, new ObjectUseRequest(entry.id(), npc.id(), 0, true));

        requestStack.assertAll(new Noop());
    }

    @Test
    void handleCantUseObject() throws Exception {
        gamePlayer().restrictions().set(Restrictions.Restriction.DENY_USE_OBJECT);
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(800));
        requestStack.clear();

        assertErrorPacket(Error.cantDoOnCurrentState(), () -> handler.handle(session, new ObjectUseRequest(entry.id(), 0, 0, false)));
    }

    @Test
    void functionalSuccess() throws Exception {
        InventoryEntry entry = explorationPlayer().inventory().add(container.get(ItemService.class).create(800));
        requestStack.clear();

        handlePacket(new ObjectUseRequest(entry.id(), 0, 0, false));

        assertEquals(1, explorationPlayer().properties().characteristics().base().get(Characteristic.AGILITY));
    }

    @Test
    void functionalErrorNotExploring() throws Exception {
        gamePlayer().stop(gamePlayer().exploration());

        InventoryEntry entry = gamePlayer().inventory().add(container.get(ItemService.class).create(800));
        requestStack.clear();

        assertThrows(CloseImmediately.class, () -> handlePacket(new ObjectUseRequest(entry.id(), 0, 0, false)));
        assertEquals(1, entry.quantity());
    }
}
