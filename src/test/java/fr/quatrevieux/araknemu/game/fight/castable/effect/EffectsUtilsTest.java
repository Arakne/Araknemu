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

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EffectsUtilsTest {
    @Test
    void isDamageEffect() {
        assertTrue(EffectsUtils.isDamageEffect(99));
        assertFalse(EffectsUtils.isDamageEffect(123));
    }

    @Test
    void isLooseApEffect() {
        assertTrue(EffectsUtils.isLooseApEffect(101));
        assertFalse(EffectsUtils.isLooseApEffect(123));
    }

    @Test
    void applyDistanceAttenuation() {
        assertEquals(1000, EffectsUtils.applyDistanceAttenuation(1000, 0));
        assertEquals(900, EffectsUtils.applyDistanceAttenuation(1000, 1));
        assertEquals(810, EffectsUtils.applyDistanceAttenuation(1000, 2));
        assertEquals(729, EffectsUtils.applyDistanceAttenuation(1000, 3));
        assertEquals(656, EffectsUtils.applyDistanceAttenuation(1000, 4));
        assertEquals(590, EffectsUtils.applyDistanceAttenuation(1000, 5));
        assertEquals(531, EffectsUtils.applyDistanceAttenuation(1000, 6));
        assertEquals(478, EffectsUtils.applyDistanceAttenuation(1000, 7));
        assertEquals(430, EffectsUtils.applyDistanceAttenuation(1000, 8));
        assertEquals(387, EffectsUtils.applyDistanceAttenuation(1000, 9));
        assertEquals(348, EffectsUtils.applyDistanceAttenuation(1000, 10));
        assertEquals(205, EffectsUtils.applyDistanceAttenuation(1000, 15));
        assertEquals(98, EffectsUtils.applyDistanceAttenuation(1000, 22));
        assertEquals(11, EffectsUtils.applyDistanceAttenuation(1000, 42));
    }
}
