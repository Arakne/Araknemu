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

package fr.quatrevieux.araknemu.game.fight.ai.factory.type;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BlockingTest extends AiBaseCase {
    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        actionFactory = new Blocking();
        dataSet.pushFunctionalSpells();
    }

    @Test
    void shouldMoveNearEnemy() {
        configureFight(b -> b
            .addSelf(fb -> fb.cell(210))
            .addEnemy(fb -> fb.cell(52))
        );

        assertEquals(11, distance(getEnemy(0)));

        generateAndPerformMove();

        assertEquals(165, fighter.cell().id());
        assertEquals(8, distance(getEnemy(0)));
    }

    @Test
    void shouldMoveNearInvokerEnemy() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(94).charac(Characteristic.AGILITY, 100))
            .addEnemy(builder -> builder.cell(124))
            .addEnemy(builder -> builder.cell(167))
        );

        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(152)); // Adjacent to enemy 167
        invoc.init();

        configureFighterAi(invoc);

        assertEquals(2, distance(getEnemy(0)));

        generateAndPerformMove();

        assertEquals(138, invoc.cell().id());
        assertEquals(1, distance(getEnemy(0)));
    }

    @Test
    void shouldDoesNothingIfAlreadyAdjacentToEnemy() {
        configureFight(fb -> fb
            .addSelf(builder -> builder.cell(94).charac(Characteristic.AGILITY, 100))
            .addEnemy(builder -> builder.cell(167))
        );

        DoubleFighter invoc = new DoubleFighter(-10, player.fighter());
        fight.fighters().joinTurnList(invoc, fight.map().get(152)); // Adjacent to enemy 167
        invoc.init();

        configureFighterAi(invoc);

        generateAction();
        assertDotNotGenerateAction();
    }

    private int distance(Fighter other) {
        return fighter.cell().coordinate().distance(other.cell());
    }
}
