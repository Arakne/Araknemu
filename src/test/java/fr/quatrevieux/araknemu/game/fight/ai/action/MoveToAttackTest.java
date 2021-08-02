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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveToAttackTest extends AiBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // @todo test "best target"
        action = MoveToAttack.nearest(container.get(Simulator.class));
    }

    @Test
    void success() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(150))
            .addEnemy(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(126))
        );

        generateAndPerformMove();

        assertEquals(122, fighter.cell().id());
    }

    @Test
    void alreadyOnValidCell() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(122))
            .addEnemy(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(126))
        );

        assertDotNotGenerateAction();
    }

    @Test
    void noMP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(150))
            .addEnemy(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(126))
        );

        removeAllMP();

        assertDotNotGenerateAction();
    }

    @Test
    void noAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(150))
            .addEnemy(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(126))
        );

        removeAllAP();

        assertDotNotGenerateAction();
    }

    @Test
    void noEnoughAP() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(150))
            .addEnemy(builder -> builder.cell(125))
            .addEnemy(builder -> builder.cell(126))
        );

        setAP(3);

        assertDotNotGenerateAction();
    }
}
