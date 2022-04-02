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
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemSetBonusTransformerTest extends TestCase {
    private ItemSetBonusTransformer transformer = new ItemSetBonusTransformer();

    @Test
    void serializeEmpty() {
        assertEquals("", transformer.serialize(Collections.emptyList()));
        assertNull(transformer.serialize(null));
    }

    @Test
    void serialize() {
        assertEquals(
            "125:25,123:45;125:50,123:100",
            transformer.serialize(
                Arrays.asList(
                    Arrays.asList(
                        new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 25, 0, 0, ""),
                        new ItemTemplateEffectEntry(Effect.ADD_CHANCE, 45, 0, 0, "")
                    ),
                    Arrays.asList(
                        new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 50, 0, 0, ""),
                        new ItemTemplateEffectEntry(Effect.ADD_CHANCE, 100, 0, 0, "")
                    )
                )
            )
        );
    }

    @Test
    void unserializeNull() {
        assertEquals(
            Collections.emptyList(),
            transformer.unserialize(null)
        );
    }

    @Test
    void unserializeEmpty() {
        assertEquals(
            Collections.emptyList(),
            transformer.unserialize("")
        );
    }

    @Test
    void unserializeWithEmptyEffects() {
        assertEquals(
            Arrays.asList(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            ),
            transformer.unserialize(";;")
        );
    }

    @Test
    void unserializeWithSingleEffect() {
        List<List<ItemTemplateEffectEntry>> bonus = transformer.unserialize(";125:10");

        assertCount(2, bonus);
        assertCount(0, bonus.get(0));
        assertCount(1, bonus.get(1));

        assertEquals(Effect.ADD_VITALITY, bonus.get(1).get(0).effect());
        assertEquals(10, bonus.get(1).get(0).min());
    }

    @Test
    void unserializeWithMultipleEffects() {
        List<List<ItemTemplateEffectEntry>> bonus = transformer.unserialize("125:25,123:45;125:50,123:100");

        assertCount(2, bonus);

        assertCount(2, bonus.get(0));
        assertEquals(Effect.ADD_VITALITY, bonus.get(0).get(0).effect());
        assertEquals(25, bonus.get(0).get(0).min());
        assertEquals(Effect.ADD_CHANCE, bonus.get(0).get(1).effect());
        assertEquals(45, bonus.get(0).get(1).min());

        assertCount(2, bonus.get(1));
        assertEquals(Effect.ADD_VITALITY, bonus.get(1).get(0).effect());
        assertEquals(50, bonus.get(1).get(0).min());
        assertEquals(Effect.ADD_CHANCE, bonus.get(1).get(1).effect());
        assertEquals(100, bonus.get(1).get(1).min());
    }

    @Test
    void unserializeError() {
        assertThrows(
            RuntimeException.class,
            () -> transformer.unserialize("invalid_data"),
            "Cannot parse item set bonus 'invalid_data'"
        );
    }
}