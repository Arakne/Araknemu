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

package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EffectToCharacteristicMappingTest extends TestCase {
    private EffectToCharacteristicMapping mapping = new EffectToCharacteristicMapping();
    
    @Test
    void createNotFound() {
        assertThrows(NoSuchElementException.class, () -> mapping.create(Effect.INFLICT_DAMAGE_FIRE, 0));
    }

    @Test
    void createPositive() {
        CharacteristicEffect effect = mapping.create(Effect.ADD_INTELLIGENCE, 15);

        assertEquals(Effect.ADD_INTELLIGENCE, effect.effect());
        assertEquals(15, effect.value());
        assertEquals(Characteristic.INTELLIGENCE, effect.characteristic());
        assertEquals(15, effect.boost());
    }

    @Test
    void createNegative() {
        CharacteristicEffect effect = mapping.create(Effect.SUB_INTELLIGENCE, 15);

        assertEquals(Effect.SUB_INTELLIGENCE, effect.effect());
        assertEquals(15, effect.value());
        assertEquals(Characteristic.INTELLIGENCE, effect.characteristic());
        assertEquals(-15, effect.boost());
    }

    @Test
    void isNegative() {
        assertTrue(mapping.isNegative(Effect.SUB_AGILITY));
        assertTrue(mapping.isNegative(Effect.SUB_CHANCE));
        assertFalse(mapping.isNegative(Effect.ADD_STRENGTH));
        assertFalse(mapping.isNegative(Effect.ADD_REDUCE_DAMAGE_EARTH));
    }

    @Test
    void createRandomWithoutMaxValue() {
        CharacteristicEffect effect = mapping.createRandom(
            new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 10, 0, 0, "")
        );

        assertEquals(Effect.ADD_AGILITY, effect.effect());
        assertEquals(10, effect.value());
        assertEquals(Characteristic.AGILITY, effect.characteristic());
        assertEquals(10, effect.boost());
    }

    @Test
    void createRandomWithMaxValue() {
        CharacteristicEffect effect = mapping.createRandom(
            new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")
        );

        assertEquals(Effect.ADD_AGILITY, effect.effect());
        assertBetween(0, 100, effect.value());

        assertNotEquals(
            mapping.createRandom(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")).value(),
            mapping.createRandom(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")).value()
        );
    }

    @Test
    void createMax() {
        CharacteristicEffect effect = mapping.createMaximize(
            new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 0, 100, 0, "")
        );

        assertEquals(Effect.ADD_AGILITY, effect.effect());
        assertEquals(100, effect.boost());
    }

    @Test
    void createMaxWithNegativeValue() {
        CharacteristicEffect effect = mapping.createMaximize(
            new ItemTemplateEffectEntry(Effect.SUB_AGILITY, 10, 100, 0, "")
        );

        assertEquals(Effect.SUB_AGILITY, effect.effect());
        assertEquals(-10, effect.boost());
    }

    @Test
    void createFromListMaximize() {
        List<CharacteristicEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.SUB_AGILITY, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.ADD_PODS, 10, 100, 0, "")
            ),
            true
        );

        assertCount(2, effects);
        assertEquals(Characteristic.AGILITY, effects.get(0).characteristic());
        assertEquals(-10, effects.get(0).boost());
        assertEquals(Characteristic.INTELLIGENCE, effects.get(1).characteristic());
        assertEquals(100, effects.get(1).boost());
    }

    @Test
    void createFromListRandom() {
        List<CharacteristicEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.SUB_AGILITY, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 10, 100, 0, ""),
                new ItemTemplateEffectEntry(Effect.ADD_PODS, 10, 100, 0, "")
            )
        );

        assertCount(2, effects);
        assertEquals(Characteristic.AGILITY, effects.get(0).characteristic());
        assertBetween(-100, -10, effects.get(0).boost());
        assertEquals(Characteristic.INTELLIGENCE, effects.get(1).characteristic());
        assertBetween(10, 100, effects.get(1).boost());
    }

    @Test
    void createFromListFixed() {
        List<CharacteristicEffect> effects = mapping.create(
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.SUB_AGILITY, 10, 0, 0, ""),
                new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 10, 0, 0, "")
            )
        );

        assertCount(2, effects);
        assertEquals(Characteristic.AGILITY, effects.get(0).characteristic());
        assertEquals(-10, effects.get(0).boost());
        assertEquals(Characteristic.INTELLIGENCE, effects.get(1).characteristic());
        assertEquals(10, effects.get(1).boost());
    }
}
