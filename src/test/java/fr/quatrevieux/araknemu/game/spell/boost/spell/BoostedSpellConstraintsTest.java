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

package fr.quatrevieux.araknemu.game.spell.boost.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.boost.MapSpellModifiers;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoostedSpellConstraintsTest extends GameBaseCase {
    private Spell spell;
    private Map<SpellsBoosts.Modifier, Integer> boostMap;
    private MapSpellModifiers modifiers;
    private BoostedSpellConstraints constraints;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        boostMap = new HashMap<>();
        modifiers = new MapSpellModifiers(3, boostMap);
        spell = container.get(SpellService.class).get(3).level(1);
        constraints = new BoostedSpellConstraints(
            spell.constraints(),
            modifiers
        );
    }

    @Test
    void rangeNotModified() {
        assertSame(spell.constraints().range(), constraints.range());
    }

    @Test
    void rangeBoosted() {
        boostMap.put(SpellsBoosts.Modifier.RANGE, 5);

        assertEquals(1, constraints.range().min());
        assertEquals(11, constraints.range().max());
    }

    @Test
    void lineLaunch() {
        assertFalse(constraints.lineLaunch());
    }

    @Test
    void lineOfSigh() {
        assertTrue(constraints.lineOfSight());

        boostMap.put(SpellsBoosts.Modifier.LINE_OF_SIGHT, 1);

        assertFalse(constraints.lineOfSight());
    }

    @Test
    void freeCell() {
        assertFalse(constraints.freeCell());
    }

    @Test
    void states() {
        assertSame(spell.constraints().requiredStates(), constraints.requiredStates());
        assertSame(spell.constraints().forbiddenStates(), constraints.forbiddenStates());
    }

    @Test
    void launchDelay() {
        assertEquals(0, constraints.launchDelay());
    }

    @Test
    void launchPerTurn() {
        boostMap.put(SpellsBoosts.Modifier.LAUNCH_PER_TURN, 10);

        assertEquals(10, constraints.launchPerTurn());
    }

    @Test
    void launchDelayDefault() throws ContainerException {
        boostMap = new HashMap<>();
        modifiers = new MapSpellModifiers(17, boostMap);
        spell = container.get(SpellService.class).get(17).level(1);
        constraints = new BoostedSpellConstraints(
            spell.constraints(),
            modifiers
        );

        assertEquals(2, constraints.launchDelay());
    }

    @Test
    void launchDelayFixed() throws ContainerException {
        boostMap = new HashMap<>();
        modifiers = new MapSpellModifiers(17, boostMap);
        spell = container.get(SpellService.class).get(17).level(1);
        constraints = new BoostedSpellConstraints(
            spell.constraints(),
            modifiers
        );

        boostMap.put(SpellsBoosts.Modifier.SET_DELAY, 1);

        assertEquals(1, constraints.launchDelay());
    }

    @Test
    void launchDelayReduce() throws ContainerException {
        boostMap = new HashMap<>();
        modifiers = new MapSpellModifiers(17, boostMap);
        spell = container.get(SpellService.class).get(17).level(1);
        constraints = new BoostedSpellConstraints(
            spell.constraints(),
            modifiers
        );

        boostMap.put(SpellsBoosts.Modifier.REDUCE_DELAY, 1);

        assertEquals(1, constraints.launchDelay());
    }

    @Test
    void launchDelayReduceAndFixed() throws ContainerException {
        boostMap = new HashMap<>();
        modifiers = new MapSpellModifiers(17, boostMap);
        spell = container.get(SpellService.class).get(17).level(1);
        constraints = new BoostedSpellConstraints(
            spell.constraints(),
            modifiers
        );

        boostMap.put(SpellsBoosts.Modifier.SET_DELAY, 1);
        boostMap.put(SpellsBoosts.Modifier.REDUCE_DELAY, 1);

        assertEquals(0, constraints.launchDelay());
    }

    @Test
    void launchPerTarget() throws ContainerException {
        boostMap = new HashMap<>();
        modifiers = new MapSpellModifiers(2, boostMap);
        spell = container.get(SpellService.class).get(2).level(1);
        constraints = new BoostedSpellConstraints(
            spell.constraints(),
            modifiers
        );

        boostMap.put(SpellsBoosts.Modifier.LAUNCH_PER_TARGET, 3);

        assertEquals(4, constraints.launchPerTarget());
    }
}
