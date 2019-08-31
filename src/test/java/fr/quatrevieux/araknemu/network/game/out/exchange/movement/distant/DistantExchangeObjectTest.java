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

package fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistantExchangeObjectTest extends GameBaseCase {
    private ItemEntry entry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        entry = explorationPlayer().inventory().add(
            container.get(ItemService.class).create(2422, true),
            3
        );
    }

    @Test
    void generateAdd() {
        assertEquals(
            "EmKO+1|2|2422|8a#f#0#0#0d0+15,7d#21#0#0#0d0+33",
            new DistantExchangeObject(entry, 2).toString()
        );
    }

    @Test
    void generateRemove() {
        assertEquals(
            "EmKO-1",
            new DistantExchangeObject(entry, 0).toString()
        );
    }
}
