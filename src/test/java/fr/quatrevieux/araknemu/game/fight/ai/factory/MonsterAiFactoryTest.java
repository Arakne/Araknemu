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

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.factory.type.Aggressive;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonsterAiFactoryTest extends FightBaseCase {
    private Fight fight;
    private MonsterAiFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createPvmFight();
        factory = new MonsterAiFactory();
        factory.register("AGGRESSIVE", new Aggressive(container.get(Simulator.class)));
    }

    @Test
    void createNotAMonster() {
        assertFalse(factory.create(player.fighter()).isPresent());
    }

    @Test
    void createSuccess() {
        assertTrue(factory.create(fight.team(1).fighters().stream().findFirst().get()).isPresent());
    }

    @Test
    void createInvalidAiType() {
        factory = new MonsterAiFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.create(fight.team(1).fighters().stream().findFirst().get()));
    }
}
