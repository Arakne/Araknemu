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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReflectedDamageTest extends FightBaseCase {
    private PlayerFighter caster;
    private PlayerFighter target;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();

        caster = player.fighter();
        target = other.fighter();

        requestStack.clear();
    }

    @Test
    void withoutModification() {
        ReflectedDamage rd = new ReflectedDamage(new Damage(15, Element.AIR).reflect(5), caster);

        assertEquals(5, rd.value());
        assertEquals(5, rd.baseValue());
        assertSame(caster, rd.target());
    }

    @Test
    void withResistance() {
        caster.characteristics().alter(Characteristic.RESISTANCE_PERCENT_AIR, 20);
        caster.characteristics().alter(Characteristic.RESISTANCE_AIR, 5);

        ReflectedDamage rd = new ReflectedDamage(new Damage(100, Element.AIR).reflect(10), caster);

        assertEquals(3, rd.value());
        assertEquals(3, rd.baseValue());
        assertSame(caster, rd.target());
    }

    @Test
    void changeTarget() {
        target.characteristics().alter(Characteristic.RESISTANCE_PERCENT_AIR, 10);
        target.characteristics().alter(Characteristic.RESISTANCE_AIR, 1);

        ReflectedDamage rd = new ReflectedDamage(new Damage(100, Element.AIR).reflect(10), caster);
        rd.changeTarget(target);

        assertEquals(8, rd.value());
        assertEquals(8, rd.baseValue());
        assertSame(target, rd.target());
    }

    @Test
    void tooHighResistanceShouldIgnoreReflectedDamage() {
        caster.characteristics().alter(Characteristic.RESISTANCE_PERCENT_AIR, 20);
        caster.characteristics().alter(Characteristic.RESISTANCE_AIR, 25);

        ReflectedDamage rd = new ReflectedDamage(new Damage(100, Element.AIR).reflect(10), caster);

        assertEquals(0, rd.value());
        assertEquals(0, rd.baseValue());
        assertSame(caster, rd.target());
    }

    @Test
    void multiply() {
        ReflectedDamage rd = new ReflectedDamage(new Damage(15, Element.AIR).reflect(5), caster);

        assertSame(rd, rd.multiply(2));

        assertEquals(5, rd.baseValue());
        assertEquals(10, rd.value());
        assertSame(caster, rd.target());

        assertSame(rd, rd.multiply(-1));

        assertEquals(5, rd.baseValue());
        assertEquals(-5, rd.value());
        assertSame(caster, rd.target());
    }
}
