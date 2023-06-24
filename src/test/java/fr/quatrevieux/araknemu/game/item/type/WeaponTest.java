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

package fr.quatrevieux.araknemu.game.item.type;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class WeaponTest extends TestCase {
    @Test
    void getters() {
        Weapon weapon = new Weapon(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
            new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
            null,
            Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
            Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
            new ArrayList<>(),
            new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
            new CellArea()
        );

        assertEquals(40, weapon.template().id());
        assertEquals(Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)), weapon.weaponEffects());
        assertEquals(Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)), weapon.characteristics());
        assertFalse(weapon.set().isPresent());
    }

    @Test
    void effects() {
        Weapon weapon = new Weapon(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
            new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
            null,
            Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
            Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
            new ArrayList<>(),
            new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
            new CellArea()
        );

        assertEquals(
            Arrays.asList(
                new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0),
                new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)
            ),
            weapon.effects()
        );
    }

    @Test
    void equalsBadType() {
        assertNotEquals(
            new Weapon(
                new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
                null,
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                Arrays.asList(new CharacteristicEffect(Effect.ADD_INTELLIGENCE, 2, +1, Characteristic.INTELLIGENCE)),
                new ArrayList<>(),
                new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
                new CellArea()
            ),
            new Object()
        );
    }

    @Test
    void equalsBadWeaponEffect() {
        assertNotEquals(
            new Weapon(
                new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
                null,
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>(),
                new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
                new CellArea()
            ),
            new Weapon(
                new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
                null,
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_EARTH, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>(),
                new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
                new CellArea()
            )
        );
    }

    @Test
    void equalsOk() {
        assertEquals(
            new Weapon(
                new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
                null,
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>(),
                new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
                new CellArea()
            ),
            new Weapon(
                new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
                null,
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>(),
                new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
                new CellArea()
            )
        );
    }

    @Test
    void equalsNull() {
        assertNotEquals(
            new Weapon(
                new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200),
                new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0)),
                null,
                Arrays.asList(new WeaponEffect(Effect.INFLICT_DAMAGE_NEUTRAL, 10, 15, 0)),
                new ArrayList<>(),
                new ArrayList<>(),
                new Weapon.WeaponInfo(4, new Interval(1, 1), 50, 30, 5, false),
                new CellArea()
            ),
            null
        );
    }
}
