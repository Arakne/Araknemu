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

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FixedTest extends AiBaseCase {
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        actionFactory = new Fixed(container.get(Simulator.class));
        dataSet.pushFunctionalSpells();
    }

    @Test
    void shouldAttackIfPossible() {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(342))
            .addEnemy(fb -> fb.cell(327))
        );

        assertCast(3, 327);
    }

    @Test
    void shouldDoNothingOtherwise() {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210).spell(27, 5))
            .addAlly(fb -> fb.cell(22))
            .addEnemy(fb -> fb.cell(52))
        );

        setMP(0);

        assertDotNotGenerateAction();
    }
}
