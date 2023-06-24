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

package fr.quatrevieux.araknemu.game.item.effect;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class WeaponEffectTest extends TestCase {
    @Test
    void getters() {
        WeaponEffect effect = new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3);

        assertEquals(Effect.INFLICT_DAMAGE_NEUTRAL, effect.effect());
        assertEquals(1, effect.min());
        assertEquals(2, effect.max());
        assertEquals(3, effect.extra());
    }

    @Test
    void equalsSameInstance() {
        WeaponEffect effect = new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3);

        assertEquals(effect, effect);
    }

    @Test
    void equalsNotSame() {
        assertEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3),
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3)
        );
    }

    @Test
    void equalsBadClass() {
        assertNotEquals(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3), new Object());
    }

    @Test
    void equalsBadEffect() {
        assertNotEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3),
            new WeaponEffect(Effect.INFLICT_DAMAGE_EARTH, 1, 2, 3)
        );
    }

    @Test
    void equalsBadMin() {
        assertNotEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3),
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 0, 2, 3)
        );
    }

    @Test
    void equalsBadMax() {
        assertNotEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3),
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 0, 3)
        );
    }

    @Test
    void equalsBadExtra() {
        assertNotEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3),
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 0)
        );
    }

    @Test
    void hashCodeEquals() {
        assertEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3).hashCode(),
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3).hashCode()
        );
    }

    @Test
    void hashCodeNotEquals() {
        assertNotEquals(
            new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3).hashCode(),
            new WeaponEffect(Effect.INFLICT_DAMAGE_EARTH, 1, 2, 3).hashCode()
        );
    }

    @Test
    void debugString() {
        assertEquals(
            "WeaponEffect{INFLICT_DAMAGE_EARTH:1, 2, 3}",
            new WeaponEffect(Effect.INFLICT_DAMAGE_EARTH, 1, 2, 3).toString()
        );
    }

    @Test
    void toTemplate() {
        ItemTemplateEffectEntry template = new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 2, 3).toTemplate();

        assertEquals(Effect.INFLICT_DAMAGE_NEUTRAL, template.effect());
        assertEquals(1, template.min());
        assertEquals(2, template.max());
        assertEquals(3, template.special());
        assertEquals("1d2+0", template.text());
    }
}
