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

package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToWeaponMapping;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeaponFactoryTest extends GameBaseCase {
    private WeaponFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new WeaponFactory(
            container.get(SpellEffectService.class),
            container.get(EffectToWeaponMapping.class),
            container.get(EffectToCharacteristicMapping.class),
            container.get(EffectToSpecialMapping.class)
        );
    }

    @Test
    void createSimple() {
        ItemType type = new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0));
        Item item = factory.create(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), type, null, false
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(1, item.effects());
        assertSame(type, item.type());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.weaponEffects());
        assertEquals(Effect.INFLICT_DAMAGE_NEUTRAL, weapon.weaponEffects().get(0).effect());
        assertEquals(1, weapon.weaponEffects().get(0).min());
        assertEquals(7, weapon.weaponEffects().get(0).max());

        assertEquals(4, weapon.info().apCost());
        assertEquals(1, weapon.info().range().min());
        assertEquals(1, weapon.info().range().max());
        assertEquals(50, weapon.info().criticalRate());
        assertEquals(30, weapon.info().failureRate());
        assertEquals(5, weapon.info().criticalBonus());
        assertFalse(weapon.info().isTwoHanded());
    }

    @Test
    void createWithRandomCharacteristic() {
        ItemType type = new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0));
        Item item = factory.create(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), type, null, false
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.characteristics());
        assertEquals(Effect.ADD_STRENGTH, weapon.characteristics().get(0).effect());
        assertTrue(weapon.characteristics().get(0).value() >= 1);
        assertTrue(weapon.characteristics().get(0).value() <= 10);
    }

    @Test
    void createWithMaxCharacteristic() {
        ItemType type = new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0));
        Item item = factory.create(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), type, null, true
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.characteristics());
        assertEquals(Effect.ADD_STRENGTH, weapon.characteristics().get(0).effect());
        assertEquals(10, weapon.characteristics().get(0).value());
    }

    @Test
    void createWithSpecialEffect() {
        ItemType type = new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0));
        Item item = factory.create(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                    new ItemTemplateEffectEntry(Effect.NULL1, 0, 0, 0, "")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), type, null, true
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.specials());
        assertEquals(Effect.NULL1, weapon.specials().get(0).effect());
    }

    @Test
    void createMissingArea() {
        ItemType type = new ItemType(6, "Épée", SuperType.WEAPON, null);
        assertThrows(IllegalArgumentException.class, () -> factory.create(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ), type, null, false
        ));
    }

    @Test
    void createMissingInfo() {
        ItemType type = new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0));
        assertThrows(IllegalArgumentException.class, () -> factory.create(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")
                ),
                20, "CS>4", 0, null, 200
            ), type, null, false
        ));
    }

    @Test
    void retrieve() {
        ItemType type = new ItemType(6, "Épée", SuperType.WEAPON, new EffectArea(EffectArea.Type.CELL, 0));
        Item item = factory.retrieve(
            new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1,
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")
                ),
                20, "CS>4", 0, "4;1;1;50;30;5;0", 200
            ),
            type,
            null,
            Arrays.asList(
                new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0"),
                new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 20, 0, 0, "")
            )
        );

        assertInstanceOf(Weapon.class, item);
        assertCount(2, item.effects());

        Weapon weapon = (Weapon) item;

        assertCount(1, weapon.weaponEffects());
        assertEquals(Effect.INFLICT_DAMAGE_NEUTRAL, weapon.weaponEffects().get(0).effect());
        assertEquals(1, weapon.weaponEffects().get(0).min());
        assertEquals(7, weapon.weaponEffects().get(0).max());
        assertSame(type, item.type());

        assertCount(1, weapon.characteristics());
        assertEquals(Effect.ADD_STRENGTH, weapon.characteristics().get(0).effect());
        assertEquals(20, weapon.characteristics().get(0).value());

        assertEquals(4, weapon.info().apCost());
        assertEquals(1, weapon.info().range().min());
        assertEquals(1, weapon.info().range().max());
        assertEquals(50, weapon.info().criticalRate());
        assertEquals(30, weapon.info().failureRate());
        assertEquals(5, weapon.info().criticalBonus());
        assertFalse(weapon.info().isTwoHanded());
    }
}
