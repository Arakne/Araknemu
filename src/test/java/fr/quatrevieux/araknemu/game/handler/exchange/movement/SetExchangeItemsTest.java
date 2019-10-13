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

package fr.quatrevieux.araknemu.game.handler.exchange.movement;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.exchange.movement.ItemsMovement;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetExchangeItemsTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());

        for (PlayerExchangeParty party : PlayerExchangeParty.make(player, other)) {
            party.start();
        }

        dataSet.pushItemTemplates().pushItemSets();
    }

    @Test
    void success() throws Exception {
        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(2422));

        handlePacket(new ItemsMovement(entry.id(), 1, 0));

        requestStack.assertLast(new LocalExchangeObject(entry, 1));
    }

    @Test
    void notExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new ItemsMovement(0, 1, 0)));
    }
}
