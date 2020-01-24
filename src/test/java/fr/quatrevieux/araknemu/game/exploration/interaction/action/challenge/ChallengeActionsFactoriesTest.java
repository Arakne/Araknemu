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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ChallengeActionsFactoriesTest extends FightBaseCase {
    private ExplorationActionRegistry factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionRegistry(new ChallengeActionsFactories(container.get(FightService.class)));

        dataSet.pushMaps().pushSubAreas().pushAreas();
    }

    @Test
    void ask() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(AskChallenge.class, action);
        assertSame(explorationPlayer(), action.performer());
        assertSame(ActionType.CHALLENGE, action.type());
        assertArrayEquals(new Object[] {other.id()}, action.arguments());
    }

    @Test
    void accept() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.ACCEPT_CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(AcceptChallenge.class, action);
        assertSame(explorationPlayer(), action.performer());
        assertSame(ActionType.ACCEPT_CHALLENGE, action.type());
        assertArrayEquals(new Object[] {other.id()}, action.arguments());
    }

    @Test
    void refuse() throws Exception {
        ExplorationPlayer other = new ExplorationPlayer(this.other);

        explorationPlayer().map().add(other);

        Action action = factory.create(explorationPlayer(), ActionType.REFUSE_CHALLENGE, new String[] {"" + other.id()});

        assertInstanceOf(RefuseChallenge.class, action);
        assertSame(explorationPlayer(), action.performer());
        assertSame(ActionType.REFUSE_CHALLENGE, action.type());
        assertArrayEquals(new Object[] {other.id()}, action.arguments());
    }
}
