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

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu._test.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FightPlacesTransformerTest extends TestCase {
    private FightPlacesTransformer transformer = new FightPlacesTransformer();

    @Test
    void unserializeSuccess() {
        int[][] places = transformer.unserialize("dKdvdgc3cPc4dhdwdZ|fIftfee1eMeyeNe2fffu");

        assertCount(2, places);

        assertEquals(9, places[0].length);
        assertEquals(10, places[1].length);

        assertCount(0, transformer.unserialize(null));
    }

    @Test
    void unserializeError() {
        assertThrows(RuntimeException.class, () -> transformer.unserialize("  invalid  "));
    }
}
