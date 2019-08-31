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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FightStartPositionsTest {
    @Test
    void generate() {
        assertEquals(
            "GPa3btbYb_cacQcRc3c5dg|aWa_blbAbCbQb5b6cjcw|0",
            new FightStartPositions(
                new List[] {
                    Arrays.asList(55, 83, 114, 127, 128, 170, 171, 183, 185, 198),
                    Arrays.asList(48, 63, 75, 90, 92, 106, 121, 122, 137, 150)
                },
                0
            ).toString()
        );
    }
}
