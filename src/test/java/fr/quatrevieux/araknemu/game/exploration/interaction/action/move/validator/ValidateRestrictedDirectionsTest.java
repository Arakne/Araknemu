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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidateRestrictedDirectionsTest extends GameBaseCase {
    private ValidateRestrictedDirections validator;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushItemTemplates()
        ;

        map = explorationPlayer().map();

        validator = new ValidateRestrictedDirections();
    }

    @Test
    void validateWithoutRestrictions() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Path<ExplorationMapCell> path = new Path<ExplorationMapCell>(
            new Decoder<>(player.map()),
            Arrays.asList(
                new PathStep(map.get(279), Direction.WEST),
                new PathStep(map.get(278), Direction.WEST)
            )
        );

        Move move = new Move(player, path, new PathValidator[0]);

        assertSame(path, validator.validate(move, move.path()));
    }

    @Test
    void validateWithRestrictionsSuccess() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        player.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);

        Path<ExplorationMapCell> path = new Path<ExplorationMapCell>(
            new Decoder<>(player.map()),
            Arrays.asList(
                new PathStep(map.get(279), Direction.WEST),
                new PathStep(map.get(294), Direction.SOUTH_EAST)
            )
        );

        Move move = new Move(player, path, new PathValidator[0]);

        assertSame(path, validator.validate(move, move.path()));
    }

    @Test
    void validateWithRestrictionsError() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        player.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);

        Path<ExplorationMapCell> path = new Path<ExplorationMapCell>(
            new Decoder<>(player.map()),
            Arrays.asList(
                new PathStep(map.get(279), Direction.WEST),
                new PathStep(map.get(278), Direction.WEST)
            )
        );

        Move move = new Move(player, path, new PathValidator[0]);

        assertThrows(IllegalArgumentException.class, () -> validator.validate(move, move.path()));
    }
}
