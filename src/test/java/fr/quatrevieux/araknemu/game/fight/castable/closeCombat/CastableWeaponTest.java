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

package fr.quatrevieux.araknemu.game.fight.castable.closeCombat;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CrossArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
        assertEquals(100, weapon.ability());
        assertEquals(service.create(40).type(), weapon.weaponType());
    }

    @Test
    void ability() {
        CastableWeapon weapon = new CastableWeapon(90, (Weapon) service.create(40));

        assertEquals(90, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(6, weapon.effects().get(0).max());
        assertEquals(5, weapon.criticalEffects().get(0).min());
        assertEquals(10, weapon.criticalEffects().get(0).max());

        weapon.increaseAbility(10);
        assertEquals(100, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(7, weapon.effects().get(0).max());
        assertEquals(6, weapon.criticalEffects().get(0).min());
        assertEquals(12, weapon.criticalEffects().get(0).max());

        weapon.increaseAbility(30);
        assertEquals(130, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(9, weapon.effects().get(0).max());
        assertEquals(7, weapon.criticalEffects().get(0).min());
        assertEquals(15, weapon.criticalEffects().get(0).max());

        weapon.decreaseAbility(50);
        assertEquals(80, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(5, weapon.effects().get(0).max());
        assertEquals(4, weapon.criticalEffects().get(0).min());
        assertEquals(9, weapon.criticalEffects().get(0).max());

        weapon.decreaseAbility(100);
        assertEquals(0, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(1, weapon.effects().get(0).max());
        assertEquals(1, weapon.criticalEffects().get(0).min());
        assertEquals(1, weapon.criticalEffects().get(0).max());
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

    @Test
    void shouldNotApplyCriticalBonusOnNullEffectParameter() throws SQLException {
        dataSet.pushItemTemplate(new ItemTemplate(
            9137,
            5,
            "Couteaux à Champignons",
            197,
            new ItemEffectsTransformer().unserialize("64#8#e#0#1d7+7,62#8#e#0#1d7+7,65#1#0#0#0d0+1,77#1f#32#0#1d20+30,7d#c9#fa#0#1d50+200,7c#15#1e#0#1d10+20,73#2#3#0#1d2+1,70#5#7#0#1d3+4,e2#1f#32#0#1d20+30,ae#12d#190#0#1d100+300,b0#b#f#0#1d5+10"),
            10,
            "CA>450",
            0,
            "3;1;1;30;50;8;1",
            57000
        ));

        CastableWeapon weapon = new CastableWeapon(90, (Weapon) service.create(9137));

        assertEquals(14, weapon.criticalEffects().get(0).min());
        assertEquals(19, weapon.criticalEffects().get(0).max());
        assertEquals(14, weapon.criticalEffects().get(1).min());
        assertEquals(19, weapon.criticalEffects().get(1).max());
        assertEquals(1, weapon.criticalEffects().get(2).min());
        assertEquals(0, weapon.criticalEffects().get(2).max());
    }

    private CastableWeapon weapon(int id) {
        return new CastableWeapon(100, (Weapon) service.create(id));
    }
}
