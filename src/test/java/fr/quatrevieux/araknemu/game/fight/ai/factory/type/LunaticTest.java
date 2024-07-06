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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.memory.AiMemory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticTest extends AiBaseCase {
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        actionFactory = new Lunatic(container.get(Simulator.class));
        dataSet.pushFunctionalSpells();
    }

    @Test
    void name() {
        assertEquals("LUNATIC", actionFactory.name());
    }

    @Test
    void shouldAttackAllyOrEnemy() throws NoSuchFieldException, IllegalAccessException {
        final Map<Integer, Integer> targetCounts = new HashMap<>();

        for (int i = 0; i < 100; ++i) {
            configureFight(b -> b
                .addSelf(fb -> fb.cell(342))
                .addEnemy(fb -> fb.cell(327))
                .addAlly(fb -> fb.cell(328))
            );
            removeSpell(6);

            Cast cast = generateCast();

            assertEquals(3, cast.spell().id());
            targetCounts.put(cast.target().id(), targetCounts.getOrDefault(cast.target().id(), 0) + 1);
        }

        assertBetween(40, 60, targetCounts.get(327));
        assertBetween(40, 60, targetCounts.get(328));
    }

    @Test
    void shouldMoveNearEnemyOrAllyIfCantAttack() throws NoSuchFieldException, IllegalAccessException {
        final Map<Integer, Integer> targetCounts = new HashMap<>();

        for (int i = 0; i < 100; ++i) {
            configureFight(b -> b
                .addSelf(fb -> fb.cell(342))
                .addEnemy(fb -> fb.cell(312))
                .addAlly(fb -> fb.cell(370))
            );
            removeAllAP();

            generateAndPerformMove();

            targetCounts.put(fighter.cell().id(), targetCounts.getOrDefault(fighter.cell().id(), 0) + 1);
        }

        assertBetween(40, 60, targetCounts.get(356));
        assertBetween(40, 60, targetCounts.get(327));
    }

    @Test
    void shouldDefineTargetAtStartTurn() throws NoSuchFieldException, IllegalAccessException {
        int targetAllyCount = 0;

        for (int i = 0; i < 100; ++i) {
            configureFight(b -> b
                .addSelf(fb -> fb.cell(342))
                .addEnemy(fb -> fb.cell(312))
                .addAlly(fb -> fb.cell(370))
            );
            removeSpell(6);

            Cast cast = generateCast();

            assertEquals(3, cast.spell().id());
            boolean targetAlly = cast.target().id() == 370;

            if (targetAlly) {
                ++targetAllyCount;
                turn.perform(cast);
                turn.terminate();

                generateAndPerformMove();
                assertEquals(356, fighter.cell().id());
            } else {
                assertEquals(312, cast.target().id());
                turn.perform(cast);
                turn.terminate();

                generateAndPerformMove();
                assertEquals(327, fighter.cell().id());
            }
        }

        assertBetween(40, 60, targetAllyCount);
    }

    @Test
    void shouldChangeTargetOnMemoryRefresh() throws NoSuchFieldException, IllegalAccessException {
        int targetAllyCount = 0;

        configureFight(b -> b
            .addSelf(fb -> fb.cell(342))
            .addEnemy(fb -> fb.cell(312))
            .addAlly(fb -> fb.cell(370))
        );
        removeSpell(6);

        Field memoryField = ai.getClass().getDeclaredField("memory");
        memoryField.setAccessible(true);
        AiMemory memory = (AiMemory) memoryField.get(ai);

        for (int i = 0; i < 100; ++i) {
            memory.refresh();
            Cast cast = generateCast();

            if (cast.target().id() == 370) {
                ++targetAllyCount;
            }
        }

        assertBetween(40, 60, targetAllyCount);
    }
}
