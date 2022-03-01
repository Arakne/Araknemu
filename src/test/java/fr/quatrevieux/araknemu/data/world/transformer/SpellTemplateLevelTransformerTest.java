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
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellTemplateLevelTransformerTest extends TestCase {
    private SpellTemplateLevelTransformer transformer = new SpellTemplateLevelTransformer(new EffectAreaTransformer());

    @Test
    void unserializeEmpty() {
        assertNull(transformer.unserialize(""));
        assertNull(transformer.unserialize(null));
    }

    @Test
    void unserializeMinusOne() {
        assertNull(transformer.unserialize("-1"));
    }

    @Test
    void unserializeValid() {
        SpellTemplate.Level level = transformer.unserialize("265,7,,,4,0,0d0+7|265,8,,,4,0,0d0+8|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|9|false");

        assertEquals(2, level.apCost());
        assertEquals(0, level.range().min());
        assertEquals(0, level.range().max());
        assertEquals(50, level.criticalHit());
        assertEquals(100, level.criticalFailure());
        assertFalse(level.lineLaunch());
        assertFalse(level.lineOfSight());
        assertFalse(level.freeCell());
        assertFalse(level.modifiableRange());
        assertEquals(0, level.classId());
        assertEquals(0, level.launchPerTurn());
        assertEquals(0, level.launchPerTarget());
        assertEquals(5, level.launchDelay());
        assertArrayEquals(new  int[0], level.requiredStates());
        assertArrayEquals(new  int[] {18, 19, 3, 1, 41}, level.forbiddenStates());
        assertEquals(9, level.minPlayerLevel());
        assertFalse(level.endsTurnOnFailure());

        assertCount(1, level.effects());
        assertCount(1, level.criticalEffects());
        assertCount(2, level.effectAreas());

        assertEquals(265, level.effects().get(0).effect());
        assertEquals(7, level.effects().get(0).min());
        assertEquals(0, level.effects().get(0).max());
        assertEquals(0, level.effects().get(0).special());
        assertEquals(4, level.effects().get(0).duration());
        assertEquals(0, level.effects().get(0).probability());
        assertEquals("0d0+7", level.effects().get(0).text());

        assertEquals(265, level.criticalEffects().get(0).effect());
        assertEquals(8, level.criticalEffects().get(0).min());
        assertEquals(0, level.criticalEffects().get(0).max());
        assertEquals(0, level.criticalEffects().get(0).special());
        assertEquals(4, level.criticalEffects().get(0).duration());
        assertEquals(0, level.criticalEffects().get(0).probability());
        assertEquals("0d0+8", level.criticalEffects().get(0).text());

        assertEquals(EffectArea.Type.CIRCLE, level.effectAreas().get(0).type());
        assertEquals(2, level.effectAreas().get(0).size());
        assertEquals(EffectArea.Type.CIRCLE, level.effectAreas().get(1).type());
        assertEquals(2, level.effectAreas().get(1).size());
    }

    @Test
    void unserializeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize("invalid"), "Cannot parse spell level 'invalid'");
    }

    @Test
    void unserializeBadRange() {
        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize("265,7,,,4,0,0d0+7|265,8,,,4,0,0d0+8|2|5|2|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|9|false"));
    }

    @Test
    void unserializeBadAreas() {
        assertThrows(IllegalArgumentException.class, () -> transformer.unserialize("265,7,,,4,0,0d0+7|265,8,,,4,0,0d0+8|2|0|0|50|100|false|false|false|false|0|0|0|5|Cc||18;19;3;1;41|9|false"));
    }

    @Test
    void serialize() {
        assertThrows(UnsupportedOperationException.class, () -> transformer.serialize(null));
    }
}
