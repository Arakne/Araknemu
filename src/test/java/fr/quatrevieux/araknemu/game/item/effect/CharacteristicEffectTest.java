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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CharacteristicEffectTest {
    @Test
    void getters() {
        CharacteristicEffect effect = new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH);

        assertEquals(Effect.SUB_STRENGTH, effect.effect());
        assertEquals(20, effect.value());
        assertEquals(-20, effect.boost());
        assertEquals(Characteristic.STRENGTH, effect.characteristic());
    }

    @Test
    void equalsSame() {
        CharacteristicEffect effect = new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH);

        assertEquals(effect, effect);
    }

    @Test
    void equalsBadClass() {
        assertNotEquals(new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH), new Object());
    }

    @Test
    void equalsBadValue() {
        assertNotEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 21, -1, Characteristic.STRENGTH),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH)
        );
    }

    @Test
    void equalsBadEffect() {
        assertNotEquals(
            new CharacteristicEffect(Effect.ADD_STRENGTH, 20, -1, Characteristic.STRENGTH),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH)
        );
    }

    @Test
    void equalsNotSame() {
        assertEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH)
        );
    }

    @Test
    void hashCodeEquals() {
        assertEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).hashCode(),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).hashCode()
        );
    }

    @Test
    void hashCodeNotEquals() {
        assertNotEquals(
            new CharacteristicEffect(Effect.SUB_STRENGTH, 21, -1, Characteristic.STRENGTH).hashCode(),
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).hashCode()
        );
    }

    @Test
    void debugString() {
        assertEquals(
            "CharacteristicEffect{SUB_STRENGTH:20}",
            new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).toString()
        );
    }

    @Test
    void toTemplate() {
        ItemTemplateEffectEntry entry = new CharacteristicEffect(Effect.SUB_STRENGTH, 20, -1, Characteristic.STRENGTH).toTemplate();

        assertEquals(Effect.SUB_STRENGTH, entry.effect());
        assertEquals(20, entry.min());
        assertEquals(0, entry.max());
        assertEquals(0, entry.special());
        assertEquals("0d0+20", entry.text());
    }
}
