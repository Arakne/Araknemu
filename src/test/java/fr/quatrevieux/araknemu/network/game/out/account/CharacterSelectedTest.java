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

package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharacterSelectedTest extends GameBaseCase {
    @Test
    void generate() throws ContainerException, SQLException {
        assertEquals("ASK|1|Bob|50|1|0|10|7b|1c8|315|", new CharacterSelected(gamePlayer()).toString());
    }

    @Test
    void generateWithItems() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        gamePlayer().inventory().add(container.get(ItemService.class).create(39), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(40), 1, 1);
        gamePlayer().inventory().add(container.get(ItemService.class).create(284), 10, -1);

        assertEquals("ASK|1|Bob|50|1|0|10|7b|1c8|315|1~27~1~0~7e#2#0#0#0d0+2;2~28~1~1~64#1#7#0#1d7+0;3~11c~a~~", new CharacterSelected(gamePlayer()).toString());
    }
}
