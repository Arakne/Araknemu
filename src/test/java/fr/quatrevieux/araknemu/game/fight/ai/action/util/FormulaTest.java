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

package fr.quatrevieux.araknemu.game.fight.ai.action.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormulaTest {
    @Test
    void sigmoid() {
        assertEquals(0.0, Formula.sigmoid(Double.NEGATIVE_INFINITY));
        assertEquals(1.0, Formula.sigmoid(Double.POSITIVE_INFINITY));
        assertEquals(0.0, Formula.sigmoid(-1_000_000_000), 0.0000001);
        assertEquals(1.0, Formula.sigmoid(1_000_000_000), 0.0000001);
        assertEquals(0.5, Formula.sigmoid(0), 0.0000001);
        assertEquals(0.9545454545454545, Formula.sigmoid(10), 0.0000001);
        assertEquals(0.04545454545454546, Formula.sigmoid(-10), 0.0000001);
        assertEquals(0.9166666666666666, Formula.sigmoid(5), 0.0000001);
        assertEquals(0.0833333333333333, Formula.sigmoid(-5), 0.0000001);
        assertEquals(0.75, Formula.sigmoid(1), 0.0000001);
        assertEquals(0.25, Formula.sigmoid(-1), 0.0000001);
        assertEquals(0.5833333333333333, Formula.sigmoid(0.2), 0.0000001);
        assertEquals(0.4166666666666666, Formula.sigmoid(-0.2), 0.0000001);
    }
}
