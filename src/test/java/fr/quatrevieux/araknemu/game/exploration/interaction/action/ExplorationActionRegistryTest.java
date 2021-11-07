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

package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge.ChallengeActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.fight.FightActionsFactories;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.MoveFactory;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExplorationActionRegistryTest extends FightBaseCase {
    private ExplorationActionRegistry factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionRegistry(
            new MoveFactory(),
            new ChallengeActionsFactories(container.get(FightService.class)),
            new FightActionsFactories(container.get(FightService.class), container.get(FighterFactory.class), container.get(SpectatorFactory.class))
        );

        dataSet.pushMaps().pushSubAreas().pushAreas();
    }

    @Test
    void createNotFound() {
        assertThrows(
            Exception.class,
            () -> factory.create(explorationPlayer(), ActionType.NONE, new String[] {}),
            "No factory found for game action : NONE"
        );
    }

    @Test
    void createSuccess() throws Exception {
        explorationPlayer().move(explorationPlayer().map().get(100), Direction.SOUTH_EAST);

        Action action = factory.create(explorationPlayer(), ActionType.MOVE, new String[] {"ebIgbf"});

        assertInstanceOf(Move.class, action);
    }

    @Test
    void register() throws Exception {
        Action action = Mockito.mock(Action.class);
        factory.register(ActionType.NONE, (player, type, arguments) -> action);

        assertSame(action, factory.create(explorationPlayer(), ActionType.NONE, new String[] {}));
    }
}
