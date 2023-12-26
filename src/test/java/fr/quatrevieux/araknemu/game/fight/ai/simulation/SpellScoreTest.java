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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellScoreTest {
    @Test
    void empty() {
        SpellScore score = new SpellScore();

        assertEquals(0, score.attackRange());
        assertEquals(0, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertEquals(0, score.score());
        assertFalse(score.isSuicide());
        assertFalse(score.isHeal());
        assertFalse(score.isAttack());
    }

    @Test
    void rangeOnConstructor() {
        SpellScore score = new SpellScore(3);

        assertEquals(3, score.attackRange());
        assertEquals(0, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertEquals(0, score.score());
        assertFalse(score.isSuicide());
        assertFalse(score.isHeal());
        assertFalse(score.isAttack());
    }

    @Test
    void damage() {
        SpellScore score = new SpellScore(0);
        score.addDamage(10);

        assertEquals(0, score.attackRange());
        assertEquals(10, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertEquals(10, score.score());
        assertFalse(score.isSuicide());
        assertFalse(score.isHeal());
        assertTrue(score.isAttack());
    }

    @Test
    void heal() {
        SpellScore score = new SpellScore(0);
        score.addHeal(10);

        assertEquals(0, score.attackRange());
        assertEquals(0, score.damage());
        assertEquals(10, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertEquals(10, score.score());
        assertFalse(score.isSuicide());
        assertTrue(score.isHeal());
        assertFalse(score.isAttack());
    }

    @Test
    void boost() {
        SpellScore score = new SpellScore(0);
        score.addBoost(10);

        assertEquals(0, score.attackRange());
        assertEquals(0, score.damage());
        assertEquals(0, score.heal());
        assertEquals(10, score.boost());
        assertEquals(0, score.debuff());
        assertEquals(10, score.score());
        assertFalse(score.isSuicide());
        assertFalse(score.isHeal());
        assertFalse(score.isAttack());
    }

    @Test
    void debuff() {
        SpellScore score = new SpellScore(0);
        score.addDebuff(10);

        assertEquals(0, score.attackRange());
        assertEquals(0, score.damage());
        assertEquals(0, score.heal());
        assertEquals(0, score.boost());
        assertEquals(10, score.debuff());
        assertEquals(10, score.score());
        assertFalse(score.isSuicide());
        assertFalse(score.isHeal());
        assertFalse(score.isAttack());
    }

    @Test
    void suicide() {
        SpellScore score = new SpellScore(0);
        assertFalse(score.isSuicide());

        score.setSuicide(true);
        assertTrue(score.isSuicide());

        score.setSuicide(false);
        assertTrue(score.isSuicide());
    }

    @Test
    void moreDamageThanHealShouldConsiderAsAttack() {
        SpellScore score = new SpellScore(0);
        score.addHeal(10);
        score.addDamage(20);

        assertEquals(0, score.attackRange());
        assertEquals(20, score.damage());
        assertEquals(10, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertEquals(30, score.score());
        assertFalse(score.isSuicide());
        assertFalse(score.isHeal());
        assertTrue(score.isAttack());
    }

    @Test
    void moreHealThanDamageShouldConsiderAsHeal() {
        SpellScore score = new SpellScore(0);
        score.addHeal(20);
        score.addDamage(10);

        assertEquals(0, score.attackRange());
        assertEquals(10, score.damage());
        assertEquals(20, score.heal());
        assertEquals(0, score.boost());
        assertEquals(0, score.debuff());
        assertEquals(30, score.score());
        assertFalse(score.isSuicide());
        assertTrue(score.isHeal());
        assertFalse(score.isAttack());
    }

    @Test
    void merge() {
        SpellScore score = new SpellScore();

        SpellScore other = new SpellScore(3);
        other.addDamage(10);
        other.addHeal(5);
        other.addBoost(3);
        other.addDebuff(1);

        score.merge(other);

        // Other is unmodified
        assertEquals(3, other.attackRange());
        assertEquals(10, other.damage());
        assertEquals(5, other.heal());
        assertEquals(3, other.boost());
        assertEquals(1, other.debuff());
        assertFalse(other.isSuicide());

        // Score will get all values from other, because they are higher
        assertEquals(3, score.attackRange());
        assertEquals(10, score.damage());
        assertEquals(5, score.heal());
        assertEquals(3, score.boost());
        assertEquals(1, score.debuff());
        assertFalse(other.isSuicide());


        other = new SpellScore(3);
        other.addDamage(5);
        other.addHeal(7);
        score.merge(other);

        // Only affect heal, because it's higher
        assertEquals(3, score.attackRange());
        assertEquals(10, score.damage());
        assertEquals(7, score.heal());
        assertEquals(3, score.boost());
        assertEquals(1, score.debuff());
        assertFalse(other.isSuicide());

        other = new SpellScore(10);
        other.addHeal(5);
        score.merge(other);

        // Do not affect attackRange, because it's heal spell
        assertEquals(3, score.attackRange());
        assertEquals(10, score.damage());
        assertEquals(7, score.heal());
        assertEquals(3, score.boost());
        assertEquals(1, score.debuff());
        assertFalse(other.isSuicide());

        other = new SpellScore(0);
        other.addDamage(20);
        other.setSuicide(true);
        score.merge(other);

        // Also merge suicide flag
        assertEquals(3, score.attackRange());
        assertEquals(20, score.damage());
        assertEquals(7, score.heal());
        assertEquals(3, score.boost());
        assertEquals(1, score.debuff());
        assertTrue(other.isSuicide());
    }

    @Test
    void multiply() {
        SpellScore score = new SpellScore(0);
        score.addDamage(10);
        score.addHeal(9);
        score.addBoost(8);
        score.addDebuff(7);
        assertSame(score, score.multiply(2));

        assertEquals(0, score.attackRange());
        assertEquals(20, score.damage());
        assertEquals(18, score.heal());
        assertEquals(16, score.boost());
        assertEquals(14, score.debuff());
    }
}
