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
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.junit.jupiter.api.Test;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RaceBaseStatsTransformerTest extends TestCase {
    private RaceBaseStatsTransformer transformer = new RaceBaseStatsTransformer(new ImmutableCharacteristicsTransformer());

    @Test
    void invalid() {
        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize(null));
        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize(""));
        assertThrows(UnsupportedOperationException.class, () -> transformer.serialize(null));
    }

    @Test
    void unserialize() {
        SortedMap<Integer, Characteristics> stats = transformer.unserialize("8:6;9:3;h:1;@1|8:7;9:3;h:1;@100");

        assertCount(2, stats.values());

        assertEquals(6, stats.get(1).get(Characteristic.ACTION_POINT));
        assertEquals(3, stats.get(1).get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, stats.get(1).get(Characteristic.MAX_SUMMONED_CREATURES));

        assertEquals(7, stats.get(100).get(Characteristic.ACTION_POINT));
        assertEquals(3, stats.get(100).get(Characteristic.MOVEMENT_POINT));
        assertEquals(1, stats.get(100).get(Characteristic.MAX_SUMMONED_CREATURES));
    }
}