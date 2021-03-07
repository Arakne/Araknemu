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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathException;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StopOnTriggerTest extends GameBaseCase {
    private StopOnTrigger validator;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        dataSet.pushTrigger(new MapTrigger(10340, 21, 0, "10300,123", ""));
        map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        validator = new StopOnTrigger();
    }

    @Test
    void onPlayerMovingWithNonWalkableCell() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Move move = new Move(
            player,
            new Path<ExplorationMapCell>(
                new Decoder<>(player.map()),
                Arrays.asList(
                    new PathStep(map.get(23), Direction.WEST),
                    new PathStep(map.get(22), Direction.WEST),
                    new PathStep(map.get(21), Direction.WEST),
                    new PathStep(map.get(20), Direction.WEST)
                )
            ),
            new PathValidator[] {new ValidateWalkable()}
        );

        Path<ExplorationMapCell> path = validator.validate(move, move.path());

        assertEquals(3, path.size());
    }

    @Test
    void onPlayerMovingWithValidPath() throws SQLException, ContainerException, PathException {
        ExplorationPlayer player = explorationPlayer();

        Move move = new Move(
            player,
            new Decoder<>(map).decode("bftdgl", map.get(279)),
            new PathValidator[] {new ValidateWalkable()}
        );

        Path<ExplorationMapCell> path = validator.validate(move, move.path());

        assertEquals("aexbftdgl", new Decoder<>(map).encode(path));
        assertEquals(map.get(395), path.get(path.size() - 1).cell());
    }

    @Test
    void shouldIgnoreTheFirstCellOnPath() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Move move = new Move(
            player,
            new Path<ExplorationMapCell>(
                new Decoder<>(player.map()),
                Arrays.asList(
                    new PathStep(map.get(21), Direction.WEST),
                    new PathStep(map.get(22), Direction.WEST),
                    new PathStep(map.get(23), Direction.WEST)
                )
            ),
            new PathValidator[] {new ValidateWalkable()}
        );

        Path<ExplorationMapCell> path = validator.validate(move, move.path());

        assertEquals(3, path.size());
        assertEquals(23, path.last().cell().id());
    }
}
