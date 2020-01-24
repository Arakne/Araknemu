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

package fr.quatrevieux.araknemu.network.game.out.area;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.Area;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea;
import fr.quatrevieux.araknemu.game.exploration.area.ExplorationSubArea;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SubAreaListTest {
    @Test
    void generate() {
        assertEquals(
            "al|1;1|3;0",
            new SubAreaList(Arrays.asList(
                new ExplorationSubArea(new SubArea(1, 2, "", true, Alignment.BONTARIAN), new Area(0, "", 0)),
                new ExplorationSubArea(new SubArea(2, 2, "", true, Alignment.NONE), new Area(0, "", 0)),
                new ExplorationSubArea(new SubArea(3, 2, "", true, Alignment.NEUTRAL), new Area(0, "", 0))
            )).toString()
        );
    }
}
