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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MoveToBoostTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new MoveToBoost(container.get(Simulator.class));
        dataSet.pushFunctionalSpells();
    }

    @Test
    void generateNotInitialized() {
        assertFalse(action.generate(Mockito.mock(AI.class), Mockito.mock(AiActionFactory.class)).isPresent());
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(298).spell(126))
            .addAlly(builder -> builder.cell(341))
            .addEnemy(builder -> builder.cell(126))
        );

        generateAndPerformMove();

        assertEquals(313, fighter.cell().id());
    }

    @Test
    void alreadyOnValidCell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(313).spell(126))
            .addAlly(builder -> builder.cell(341))
            .addEnemy(builder -> builder.cell(126))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void noMP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(298).spell(126))
            .addAlly(builder -> builder.cell(341))
            .addEnemy(builder -> builder.cell(126))
        );

        removeAllMP();

        assertDotNotGenerateAction();
    }

    @Test
    void noAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(298).spell(126))
            .addAlly(builder -> builder.cell(341))
            .addEnemy(builder -> builder.cell(126))
        );

        removeAllAP();

        assertDotNotGenerateAction();
    }

    @Test
    void noEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(298).spell(126))
            .addAlly(builder -> builder.cell(341))
            .addEnemy(builder -> builder.cell(126))
        );

        setAP(1);

        assertDotNotGenerateAction();
    }

    @Test
    void shouldNotMoveIfBlockedByOtherFighters() throws NoSuchFieldException, IllegalAccessException {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(298).spell(126))
            .addAlly(builder -> builder.cell(341))
            .addEnemy(builder -> builder.cell(312))
        );

        removeSpell(6);

        assertDotNotGenerateAction();
    }

    @Test
    void shouldMoveIfCanBoostDespiteEnemyOnAdjacentCell() throws NoSuchFieldException, IllegalAccessException {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(342).spell(27).charac(Characteristic.AGILITY, 100))
            .addEnemy(builder -> builder.cell(356)) // Adjacent
            .addAlly(builder -> builder.cell(384))
        );

        removeSpell(6);

        generateAndPerformMove();

        assertEquals(327, fighter.cell().id());
    }
}
