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

import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.MapCell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FightStartPositionsTest {
    @Test
    void generate() {
        class FakeCell implements MapCell {
            private final int id;

            public FakeCell(int id) {
                this.id = id;
            }

            @Override
            public int id() {
                return id;
            }

            @Override
            public boolean walkable() {
                return false;
            }

            @Override
            public DofusMap map() {
                return null;
            }
        }

        assertEquals(
            "GPa3btbYb_cacQcRc3c5dg|aWa_blbAbCbQb5b6cjcw|0",
            new FightStartPositions(
                new MapCell[][] {
                    new MapCell[] {new FakeCell(55), new FakeCell(83), new FakeCell(114), new FakeCell(127), new FakeCell(128), new FakeCell(170), new FakeCell(171), new FakeCell(183), new FakeCell(185), new FakeCell(198)},
                    new MapCell[] {new FakeCell(48), new FakeCell(63), new FakeCell(75), new FakeCell(90), new FakeCell(92), new FakeCell(106), new FakeCell(121), new FakeCell(122), new FakeCell(137), new FakeCell(150)}
                },
                0
            ).toString()
        );
    }
}
