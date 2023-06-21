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

package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CrossArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastableWeaponTest extends GameBaseCase {
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushWeaponTemplates()
            .pushItemSets()
        ;

        service = container.get(ItemService.class);
    }

    @Test
    void values() {
        CastableWeapon weapon = weapon(40);

        assertEquals(4, weapon.apCost());
        assertEquals(30, weapon.criticalFailure());
        assertEquals(50, weapon.criticalHit());
    }

    @Test
    void effectsHammer() {
        CastableWeapon weapon = weapon(2416);

        assertCount(2, weapon.effects());

        assertEquals(4, weapon.effects().get(0).min());
        assertEquals(8, weapon.effects().get(0).max());
        assertEquals(0, weapon.effects().get(0).special());
        assertEquals("", weapon.effects().get(0).text());
        assertEquals(97, weapon.effects().get(0).effect());
        assertEquals(0, weapon.effects().get(0).duration());
        assertEquals(0, weapon.effects().get(0).probability());
        assertEquals(WeaponEffectTarget.INSTANCE, weapon.effects().get(0).target());
        assertEquals(0, weapon.effects().get(0).boost());
        assertEquals(1, weapon.effects().get(0).area().size());
        assertInstanceOf(CrossArea.class, weapon.effects().get(0).area());

        assertEquals(4, weapon.effects().get(1).min());
        assertEquals(8, weapon.effects().get(1).max());
        assertEquals(0, weapon.effects().get(1).special());
        assertEquals("", weapon.effects().get(1).text());
        assertEquals(99, weapon.effects().get(1).effect());
        assertEquals(0, weapon.effects().get(1).duration());
        assertEquals(0, weapon.effects().get(1).probability());
        assertEquals(WeaponEffectTarget.INSTANCE, weapon.effects().get(1).target());
        assertEquals(0, weapon.effects().get(1).boost());
        assertEquals(1, weapon.effects().get(1).area().size());
        assertInstanceOf(CrossArea.class, weapon.effects().get(1).area());
    }

    @Test
    void effectsSword() {
        CastableWeapon weapon = weapon(40);

        assertCount(1, weapon.effects());

        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(7, weapon.effects().get(0).max());
        assertEquals(0, weapon.effects().get(0).special());
        assertEquals("", weapon.effects().get(0).text());
        assertEquals(100, weapon.effects().get(0).effect());
        assertEquals(0, weapon.effects().get(0).duration());
        assertEquals(0, weapon.effects().get(0).probability());
        assertEquals(WeaponEffectTarget.INSTANCE, weapon.effects().get(0).target());
        assertEquals(0, weapon.effects().get(0).boost());
        assertEquals(0, weapon.effects().get(0).area().size());
        assertInstanceOf(CellArea.class, weapon.effects().get(0).area());
    }

    @Test
    void criticalEffect() {
        CastableWeapon weapon = weapon(40);

        assertCount(1, weapon.criticalEffects());

        assertEquals(6, weapon.criticalEffects().get(0).min());
        assertEquals(12, weapon.criticalEffects().get(0).max());
        assertEquals(0, weapon.criticalEffects().get(0).special());
        assertEquals("", weapon.criticalEffects().get(0).text());
        assertEquals(100, weapon.criticalEffects().get(0).effect());
        assertEquals(0, weapon.criticalEffects().get(0).duration());
        assertEquals(0, weapon.criticalEffects().get(0).probability());
        assertEquals(WeaponEffectTarget.INSTANCE, weapon.criticalEffects().get(0).target());
        assertEquals(0, weapon.criticalEffects().get(0).boost());
        assertEquals(0, weapon.criticalEffects().get(0).area().size());
        assertInstanceOf(CellArea.class, weapon.criticalEffects().get(0).area());
    }

    @Test
    void constraintsHammer() {
        CastableWeapon weapon = weapon(40);

        SpellConstraints constraints = weapon.constraints();

        assertInstanceOf(WeaponConstraints.class, constraints);

        assertEquals(1, constraints.range().min());
        assertEquals(1, constraints.range().max());
        assertFalse(constraints.lineLaunch());
        assertTrue(constraints.lineOfSight());
        assertFalse(constraints.freeCell());
        assertEquals(0, constraints.launchPerTurn());
        assertEquals(0, constraints.launchPerTarget());
        assertEquals(0, constraints.launchDelay());
        assertArrayEquals(new int[0], constraints.requiredStates());
        assertArrayEquals(new int[] {1, 3, 18, 42}, constraints.forbiddenStates());
    }

    @Test
    void constraintsBow() {
        CastableWeapon weapon = weapon(89);

        SpellConstraints constraints = weapon.constraints();

        assertInstanceOf(WeaponConstraints.class, constraints);

        assertEquals(2, constraints.range().min());
        assertEquals(6, constraints.range().max());
        assertFalse(constraints.lineLaunch());
        assertTrue(constraints.lineOfSight());
        assertFalse(constraints.freeCell());
        assertEquals(0, constraints.launchPerTurn());
        assertEquals(0, constraints.launchPerTarget());
        assertEquals(0, constraints.launchDelay());
        assertArrayEquals(new int[0], constraints.requiredStates());
        assertArrayEquals(new int[] {1, 3, 18, 42}, constraints.forbiddenStates());
    }

    private CastableWeapon weapon(int id) {
        return new CastableWeapon((Weapon) service.create(id));
    }
}