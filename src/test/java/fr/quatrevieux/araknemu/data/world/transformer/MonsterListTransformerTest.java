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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonsterListTransformerTest extends TestCase {
    @Test
    void invalid() {
        MonsterListTransformer transformer = new MonsterListTransformer();

        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize(null));
        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize(""));
        assertThrows(UnsupportedOperationException.class, () -> transformer.serialize(Collections.emptyList()));
    }

    @Test
    void unserialize() {
        MonsterListTransformer transformer = new MonsterListTransformer();

        List<MonsterGroupData.Monster> list = transformer.unserialize("31|34,10|36,3,6");

        assertCount(3, list);

        assertEquals(31, list.get(0).id());
        assertEquals(new Interval(1, Integer.MAX_VALUE), list.get(0).level());
        assertEquals(1, list.get(0).rate());

        assertEquals(34, list.get(1).id());
        assertEquals(new Interval(10, 10), list.get(1).level());
        assertEquals(1, list.get(1).rate());

        assertEquals(36, list.get(2).id());
        assertEquals(new Interval(3, 6), list.get(2).level());
        assertEquals(1, list.get(2).rate());
    }

    @Test
    void unserializeWithRate() {
        MonsterListTransformer transformer = new MonsterListTransformer();

        List<MonsterGroupData.Monster> list = transformer.unserialize("31x3|34,10x2");

        assertEquals(3, list.get(0).rate());
        assertEquals(2, list.get(1).rate());
    }
}