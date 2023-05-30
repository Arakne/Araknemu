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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateCellsTest {
    @Test
    void singleCell() {
        UpdateCells packet = new UpdateCells(UpdateCells.Data.fromProperties(
            123,
            true,
            UpdateCells.ACTIVE.set(true),
            UpdateCells.LINE_OF_SIGHT.set(true),
            UpdateCells.LAYER_2_OBJECT_NUMBER.set(25),
            UpdateCells.LAYER_2_OBJECT_INTERACTIVE.set(false)
        ));

        assertEquals("GDC123;Haaaaaaaaz3005;1", packet.toString());
    }

    @Test
    void multipleCell() {
        UpdateCells packet = new UpdateCells(
            UpdateCells.Data.fromProperties(
                123,
                true,
                UpdateCells.LINE_OF_SIGHT.set(true),
                UpdateCells.LAYER_2_OBJECT_NUMBER.set(25)
            ),
            UpdateCells.Data.fromProperties(
                214,
                false,
                UpdateCells.LAYER_1_OBJECT_NUMBER.set(42)
            )
        );

        assertEquals("GDC123;baaaaaaaaz1004;1|214;aaaaaaQaaa20;0", packet.toString());
    }

    @Test
    void reset() {
        UpdateCells packet = new UpdateCells(UpdateCells.Data.reset(123));

        assertEquals("GDC123", packet.toString());
    }
}
