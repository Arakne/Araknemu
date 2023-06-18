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

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Blocking;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.DoubleFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleAiFactoryTest extends FightBaseCase {
    private Fight fight;
    private DoubleAiFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        factory = new DoubleAiFactory(new Blocking());
    }

    @Test
    void createNotADouble() {
        assertFalse(factory.create(player.fighter()).isPresent());
    }

    @Test
    void createSuccess() {
        DoubleFighter fighter = new DoubleFighter(-5, player.fighter());
        fight.nextState();
        fight.fighters().joinTurnList(fighter, fight.map().get(123));

        assertTrue(factory.create(fighter).isPresent());
    }
}
