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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamageTest {
    private Damage damage;

    @BeforeEach
    void setUp() {
        damage = new Damage(15, Element.EARTH);
    }

    @Test
    void defaultValue() {
        assertEquals(15, damage.value());
    }

    @Test
    void fixed() {
        assertEquals(10, damage.fixed(5).value());
    }

    @Test
    void fixedHigherThanValue() {
        assertEquals(0, damage.fixed(20).value());
    }

    @Test
    void percent() {
        assertEquals(12, damage.percent(20).value());
    }

    @Test
    void percentHigherThan100() {
        assertEquals(0, damage.percent(75).percent(30).value());
    }

    @Test
    void fixedAndPercent() {
        assertEquals(7, damage.percent(20).fixed(5).value());
    }

    @Test
    void multiplyPositive() {
        assertEquals(21, damage.percent(20).fixed(5).multiply(3).value());
    }

    @Test
    void multiplyNegative() {
        assertEquals(-7, damage.percent(20).fixed(5).multiply(-1).value());
    }

    @Test
    void reduce() {
        assertEquals(7, damage.percent(20).reduce(5).value());
        assertEquals(5, damage.reducedDamage());
    }
}
