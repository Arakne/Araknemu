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

package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddSpritesTest extends GameBaseCase {
    @Test
    void generateWithPlayer() throws Exception {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        ExplorationPlayer p1 = explorationPlayer();
        ExplorationPlayer p2 = new ExplorationPlayer(makeOtherPlayer());
        p2.changeMap(p1.map(), 210);

        p1.inventory().add(container.get(ItemService.class).create(2416), 1, 1);

        assertEquals(
            "GM|+279;1;0;1;Bob;1;10^100x100;0;;7b;1c8;315;970,,,,;;;;;;8;|+210;1;0;2;Other;9;90^100x100;0;;-1;-1;-1;,,,,;;;;;;8;",
            new AddSprites(Arrays.asList(p1.sprite(), p2.sprite())).toString()
        );
    }
}
